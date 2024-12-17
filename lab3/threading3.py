import concurrent.futures
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

    with concurrent.futures.ThreadPoolExecutor(max_workers=5) as executor:
        executor.map(worker, range(task_count))

    end = time.time()
    print("Time taken with multi-threads:", end - start)