# cpu-process-scheduler

This is a CPU Process Scheduler program that I developed for my Operating Systems course. This Java program simulates a simple process scheduler. The scheduler implements the First Come First Serve (FCFS) scheduling algorithm and calculates the average response time, average waiting time, and average turnaround time for all processes.

The program creates 9 processes (ProcessClass objects), each with a name, a list of burst times, and a list of I/O times. The program then adds all processes to the scheduler (Scheduler object) and runs the simulation.

When a process is added to the scheduler, it is added to a ready queue. The scheduler then runs the processes in the ready queue in FCFS fashion, with each process execution depending on its next arrival time. If a process completes all of its bursts and I/O times, it is marked as complete, and the scheduler moves on to the next process in the ready queue. If a process completes a burst but still has I/O time remaining, it is moved to a waiting queue until its I/O time has completed.

The ProcessClass class contains information about each process, such as its arrival time, burst and I/O times, response time, wait time, and turnaround time. The Scheduler class contains methods for adding processes to the ready queue, running the scheduler, and calculating the average response time, average waiting time, average turnaround time, and CPU utilization.
