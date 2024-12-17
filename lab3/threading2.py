from threading import Thread
from compute import io_bound
import time

io_duration = 1
task_count = 50

def worker(num):
    print(f'Begin Worker: {num}')
    st = time.time()
    io_bound(io_duration)
    print(f'End Worker: {num} in {time.time() - st} seconds')

if __name__ == '__main__':
    start = time.time()
    threads = [Thread(target=worker, args=(i,)) for i in range(task_count)]

    for thread in threads:
        thread.start()

    for thread in threads:
        thread.join()

    end = time.time()
    print("Time taken with multi-threads:", end - start)