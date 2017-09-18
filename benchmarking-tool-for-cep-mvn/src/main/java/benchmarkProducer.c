// Compile with 'gcc thisfile.c -lpthread -lrt -Wall'
#define _GNU_SOURCE
#include <malloc.h>
#include <sys/resource.h>	// needed for getrusage
#include <limits.h>
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <linux/unistd.h>
#include <linux/kernel.h>
#include <linux/types.h>
#include <sys/syscall.h>
#include <pthread.h>
#include <sys/time.h>
#include <stdbool.h>
#include <sys/mman.h>
#include <stdbool.h>
#include "JniProducerCaller.h"


#define PRE_ALLOCATION_SIZE (100*1024*1024) /* 100MB pagefault free buffer */
#define MY_STACK_SIZE       (100*1024)      /* 100 kB is enough for now. */


#define CHUNK 1024 /* read 1024 bytes at a time */

#define gettid() syscall(__NR_gettid)

#define SCHED_DEADLINE	6

/* XXX use the proper syscall numbers */
#ifdef __x86_64__
#define __NR_sched_setattr		314
#define __NR_sched_getattr		315
#endif

#ifdef __i386__
#define __NR_sched_setattr		351
#define __NR_sched_getattr		352
#endif

#ifdef __arm__
#define __NR_sched_setattr		380
#define __NR_sched_getattr		381
#endif

static volatile int done;

struct sched_attr {
__u32 size;

__u32 sched_policy;
__u64 sched_flags;

/* SCHED_NORMAL, SCHED_BATCH */
__s32 sched_nice;

/* SCHED_FIFO, SCHED_RR */
__u32 sched_priority;

/* SCHED_DEADLINE (nsec) */
__u64 sched_runtime;
__u64 sched_deadline;
__u64 sched_period;
};

struct arg_struct {
  int threadId;
  long int startTimeSeconds;
  long int startTimeMicroseconds;
  long int sleepTimeMicroseconds;
  int numberOfLoops;
  int portNumber;
};


#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>

void error(char *msg)
{
    perror(msg);
    exit(0);
}

int sendTcpMessage(int pPortNumber, bool benchmarkFinished)
{
    int sockfd, portno, n;

    struct sockaddr_in serv_addr;
    struct hostent *server;


    // TODO: insert randomly generated number
    char buffer[256];

    if (benchmarkFinished)
      strcpy(buffer, "benchmarkFinished");
    else
      sprintf(buffer, "%d", 1);

    // printf(benchmarkFinished ? "Benchmark is finished." : "Benchmark is not finished");
    sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if (sockfd < 0)
        error("ERROR opening socket");
    server = gethostbyname("localhost");
    if (server == NULL) {
        fprintf(stderr,"ERROR, no such host\n");
        exit(0);
    }
    bzero((char *) &serv_addr, sizeof(serv_addr));
    serv_addr.sin_family = AF_INET;
    bcopy((char *)server->h_addr,
         (char *)&serv_addr.sin_addr.s_addr,
         server->h_length);
    serv_addr.sin_port = htons(pPortNumber);
    if (connect(sockfd,(struct sockaddr *)&serv_addr,sizeof(serv_addr)) < 0)
        error("ERROR connecting");
    //bzero(buffer,256);
    //fgets(buffer,255,stdin);
    n = write(sockfd,buffer,strlen(buffer));
    if (n < 0)
         error("ERROR writing to socket");
    //bzero(buffer,256);
    //n = read(sockfd,buffer,255);
    //if (n < 0)
         //error("ERROR reading from socket");
    //printf("%s\n",buffer);
    close(sockfd);
    return 0;
}

static void setprio(int prio, int sched)
{
	struct sched_param param;
	// Set realtime priority for this thread
	param.sched_priority = prio;
	if (sched_setscheduler(0, sched, &param) < 0)
		perror("sched_setscheduler");
}

void show_new_pagefault_count(const char* logtext,
			      const char* allowed_maj,
			      const char* allowed_min)
{
	static int last_majflt = 0, last_minflt = 0;
	struct rusage usage;

	getrusage(RUSAGE_SELF, &usage);

	//printf("%-30.30s: Pagefaults, Major:%ld (Allowed %s), " \
	//       "Minor:%ld (Allowed %s)\n", logtext,
	//       usage.ru_majflt - last_majflt, allowed_maj,
	//       usage.ru_minflt - last_minflt, allowed_min);

	last_majflt = usage.ru_majflt;
	last_minflt = usage.ru_minflt;
}

static void prove_thread_stack_use_is_safe(int stacksize)
{
	volatile char buffer[stacksize];
	int i;

	/* Prove that this thread is behaving well */
	for (i = 0; i < stacksize; i += sysconf(_SC_PAGESIZE)) {
		/* Each write to this buffer shall NOT generate a
			pagefault. */
		buffer[i] = i;
	}

	show_new_pagefault_count("Caused by using thread stack", "0", "0");
}

/*************************************************************/
/* The thread to start */
static void *my_rt_thread(void *args)
{
	struct timespec ts;
	ts.tv_sec = 30;
	ts.tv_nsec = 0;

	setprio(sched_get_priority_max(SCHED_RR), SCHED_RR);

	//printf("I am an RT-thread with a stack that does not generate " \
	//       "page-faults during use, stacksize=%i\n", MY_STACK_SIZE);

//<do your RT-thing here>

	show_new_pagefault_count("Caused by creating thread", ">=0", ">=0");

	prove_thread_stack_use_is_safe(MY_STACK_SIZE);

	/* wait 30 seconds before thread terminates */
	clock_nanosleep(CLOCK_REALTIME, 0, &ts, NULL);

	return NULL;
}


static void configure_malloc_behavior(void)
{
	/* Now lock all current and future pages
	   from preventing of being paged */
	if (mlockall(MCL_CURRENT | MCL_FUTURE))
		perror("mlockall failed:");

	/* Turn off malloc trimming.*/
	mallopt(M_TRIM_THRESHOLD, -1);

	/* Turn off mmap usage. */
	mallopt(M_MMAP_MAX, 0);
}

static void reserve_process_memory(int size)
{
	int i;
	char *buffer;

	buffer = malloc(size);

	/* Touch each page in this piece of memory to get it mapped into RAM */
	for (i = 0; i < size; i += sysconf(_SC_PAGESIZE)) {
		/* Each write to this buffer will generate a pagefault.
		   Once the pagefault is handled a page will be locked in
		   memory and never given back to the system. */
		buffer[i] = 0;
	}

	/* buffer will now be released. As Glibc is configured such that it
	   never gives back memory to the kernel, the memory allocated above is
	   locked for this process. All malloc() and new() calls come from
	   the memory pool reserved and locked above. Issuing free() and
	   delete() does NOT make this locking undone. So, with this locking
	   mechanism we can build C++ applications that will never run into
	   a major/minor pagefault, even with swapping enabled. */
	free(buffer);
}


int sched_setattr(pid_t pid,
	  const struct sched_attr *attr,
	  unsigned int flags)
{
  return syscall(__NR_sched_setattr, pid, attr, flags);
}

int sched_getattr(pid_t pid,
	  struct sched_attr *attr,
	  unsigned int size,
	  unsigned int flags)
{
  return syscall(__NR_sched_getattr, pid, attr, size, flags);
}

void *run_deadline(void *data)
{
  struct sched_attr attr;
  int x = 0;
  int ret;
  unsigned int flags = 0;

  // unpacking data
  struct   arg_struct *args      = data;
  int      threadId              = args->threadId;
  int      numberOfLoops         = args->numberOfLoops;
  int      portNumber            = args->portNumber; 
  long int startTimeSeconds      = args->startTimeSeconds;
  long int startTimeMicroseconds = args->startTimeMicroseconds;
  long int sleepTimeMicroseconds = args->sleepTimeMicroseconds;

  struct timeval tv;
  gettimeofday(&tv, NULL);

  char buffer[32];
  snprintf(buffer, sizeof(char) * 32, "files/file%i.txt", threadId);



  //printf("deadline thread started [%ld]\n", gettid());

  attr.size = sizeof(attr);
  attr.sched_flags = 0;
  attr.sched_nice = 0;
  attr.sched_priority = 0;

  /* This creates a 50us/1ms reservation */
  attr.sched_policy = SCHED_DEADLINE;
  attr.sched_runtime = 1 * 50 * 1000;
  attr.sched_period = attr.sched_deadline = 1 * 1000 * 1000;

  ret = sched_setattr(0, &attr, flags);
  if (ret < 0) {
  	done = 0;
  	perror("sched_setattr");
  	exit(-1);
  }

  // testing threads
  // printf("Thread %d Received Start Time: %ld, %ld\n", threadId, startTimeSeconds, startTimeMicroseconds);
  // printf("Thread %d Current Time: %ld, %ld \n\n", threadId, tv.tv_sec, tv.tv_usec);

  while (tv.tv_usec % 1000 >= 500) {
    gettimeofday(&tv, NULL);
  }
  // printf("Thread %d Starting to count to start time at %ld, %ld \n\n", threadId, tv.tv_sec, tv.tv_usec);


  int i = 0;
  while (true) {

     while((startTimeSeconds - tv.tv_sec) > 0 || (startTimeMicroseconds / 1000 - tv.tv_usec / 1000) > 0) {
      usleep(1);
      // nanosleep(&tim , &tim2);
      // nanosleep((const struct timespec[]){{0, 100L}}, NULL);
      gettimeofday(&tv, NULL);
    }


    // fprintf(f, "Thread %d, Loop %d, 'New Time: %ld seconds %ld milliseconds' \n", threadId, i, tv.tv_sec, tv.tv_usec / 1000);
    // fprintf(f, "Loop %d, 'New Time: %ld seconds %ld milliseconds' \n", i, tv.tv_sec, tv.tv_usec / 1000);

    bool benchmarkFinished = false; 
    if (i == numberOfLoops)
      benchmarkFinished = true; 
    sendTcpMessage(portNumber, benchmarkFinished);

    //incrementing time and steps
    if (sleepTimeMicroseconds >= 1000000) {
     startTimeSeconds += sleepTimeMicroseconds / 1000000;
     startTimeMicroseconds += sleepTimeMicroseconds % 1000000;
    }
    else
      startTimeMicroseconds += sleepTimeMicroseconds;


    if (i == numberOfLoops) {
      break;
    }
    i++;
  }
  // endof testing threads


  // struct timeval tv;
  //
  // while (!done) {
  //   gettimeofday(&tv, NULL);
  //  printf("Thread %d: %ld %ld\n", threadId, tv.tv_sec, tv.tv_usec);
  // 	x++;
  // }


  // printf("deadline thread dies [%ld]\n", gettid());
  return NULL;
}

char * readFile(const char * filename)
{
  char * buffer = 0;
  long length;
  FILE * f = fopen (filename, "rb");

  if (f)
  {
    fseek (f, 0, SEEK_END);
    length = ftell (f);
    fseek (f, 0, SEEK_SET);
    buffer = malloc (length);
    if (buffer)
    {
      fread (buffer, 1, length, f);
    }
    fclose (f);
  }

  if (buffer)
  {
    return buffer;
  }
}

void checkAllFiles(int pNumberOfThreads)
{
  bool allFilesMatch = true;

  char * bufferFile0 = 0;
  bufferFile0 = readFile("files/file0.txt");

  for (int i = 1; i < pNumberOfThreads; i++) {
    char * bufferIterFile = 0;
    char buf[12];
    sprintf(buf, "files/file%d.txt", i); // puts string into buffer
    bufferIterFile = readFile(buf);

    // subbuff[4] = '\0';

    if (strcmp(bufferFile0, bufferIterFile) != 0)
    {
    //  printf("%s\n", "\033[31;1m");
    //  printf("Thread %d\n", 0);
    //  printf("%s\n", bufferFile0);
    //  printf("ERROR in Thread %d\n", i);
    //  printf("%s\n", bufferIterFile);

      allFilesMatch = false;

      break;
    }
  }

  if (allFilesMatch) {
  //  printf("%s\n", "\033[32;1m");
  //  printf("%s\n", "All Threads match.");
  //  printf("Thread %d\n", 0);
  //  printf("%s\n", bufferFile0);
  }


}


// int main (int argc, char **argv)
JNIEXPORT void JNICALL Java_JniProducerCaller_startBenchmark(JNIEnv * env, jobject  obj)
{
  show_new_pagefault_count("Initial count", ">=0", ">=0");

  configure_malloc_behavior();

  show_new_pagefault_count("mlockall() generated", ">=0", ">=0");

  reserve_process_memory(PRE_ALLOCATION_SIZE);

  show_new_pagefault_count("malloc() and touch generated",
  			 ">=0", ">=0");

  /* Now allocate the memory for the 2nd time and prove the number of
     pagefaults are zero */
  reserve_process_memory(PRE_ALLOCATION_SIZE);
  show_new_pagefault_count("2nd malloc() and use generated",
  			 "0", "0");

//  printf("\n\nLook at the output of ps -leyf, and see that the " \
//         "RSS is now about %d [MB]\n",
//         PRE_ALLOCATION_SIZE / (1024 * 1024));

  int NUMBER_OF_THREADS                 = 35;
  int SLEEP_TIME_MILLISECONDS           = 1;
  int NUMBER_OF_LOOPS                   = 10;
  // important for thread synchronization, depends on thread number
  // how to determine automatically?
  int MAX_NUMBER_OF_PASSED_MICROSECONDS = 500;
  int START_TIME_LAG_MICROSECONDS       = NUMBER_OF_THREADS * 275000;
  int startPortNumber                   = 1234;

  struct timeval tv;
  pthread_t thread_id[NUMBER_OF_THREADS];
  struct arg_struct args_Thread[NUMBER_OF_THREADS];

  gettimeofday(&tv, NULL);
  while (tv.tv_usec % 1000 >= MAX_NUMBER_OF_PASSED_MICROSECONDS) {
    gettimeofday(&tv, NULL);
  }

  long int startTimeSeconds      = tv.tv_sec;
  long int startTimeMicroseconds = tv.tv_usec;

//  printf("Current Time: %ld %ld \n", startTimeSeconds, startTimeMicroseconds);


  // incrementing time and steps
  if (tv.tv_usec + START_TIME_LAG_MICROSECONDS >= 1000000) {
    startTimeSeconds      = tv.tv_sec + (tv.tv_usec + START_TIME_LAG_MICROSECONDS) / 1000000;
    startTimeMicroseconds = (tv.tv_usec + START_TIME_LAG_MICROSECONDS) % 1000000;
  }
  else
    startTimeMicroseconds += START_TIME_LAG_MICROSECONDS;


//  printf("main thread [%ld]\n", gettid());


  int i = 0;
  for (i = 0; i < NUMBER_OF_THREADS; i++) {

    args_Thread[i].threadId              = i;
    args_Thread[i].startTimeSeconds      = startTimeSeconds;
    args_Thread[i].startTimeMicroseconds = startTimeMicroseconds;
    args_Thread[i].sleepTimeMicroseconds = SLEEP_TIME_MILLISECONDS * 1000L;
    args_Thread[i].numberOfLoops         = NUMBER_OF_LOOPS;
    args_Thread[i].portNumber            = startPortNumber + i;

    pthread_create( &thread_id[i], NULL, run_deadline, (void *)&args_Thread[i]);
  }

  sleep(1);

  done = 1;

  for (i = 0; i < NUMBER_OF_THREADS; i++) {
    pthread_join( thread_id[i], NULL);
  }

  //checkAllFiles(NUMBER_OF_THREADS);

//  printf("Start time: %ld seconds, %ld microseconds\n", startTimeSeconds, startTimeMicroseconds);

//  printf("main dies [%ld]\n", gettid());
}
