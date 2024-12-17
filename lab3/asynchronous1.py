import asyncio
from compute import io_async
import time

io_duration = 1
worker_count = 5

async def worker(num):
    print(f'Begin Worker: {num}')
    st = time.time()
    await io_async(io_duration)
    print(f'End Worker: {num} in {time.time() - st} seconds')

async def main():
    start = time.time()
    tasks = [asyncio.create_task(worker(i)) for i in range(worker_count)]

    await asyncio.gather(*tasks)

    end = time.time()
    print("Time taken with asyncio:", end - start)

asyncio.run(main())