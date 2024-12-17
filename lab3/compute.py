def cpu_bound(count):
    for _ in range(count):
        pass

def io_bound(seconds_delay):
    import time
    time.sleep(seconds_delay)

async def io_async(seconds_delay):
    import asyncio
    await asyncio.sleep(seconds_delay)