from multiprocessing import Process
from compute import cpu_bound
import time

op_count = 10**8
worker_count = 5

def worker(num):
    print(f'Begin Worker: {num}')
    st = time.time()
    cpu_bound(op_count)
    print(f'End Worker: {num} in {time.time() - st} seconds')

if __name__ == '__main__':
    start = time.time()
    processes = [Process(target=worker, args=(i,)) for i in range(worker_count)]

    for process in processes:
        process.start()

    for process in processes:
        process.join()

    end = time.time()
    print("Time taken with multi-threads:", end - start)