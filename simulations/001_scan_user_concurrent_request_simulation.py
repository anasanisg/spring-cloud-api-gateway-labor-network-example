import requests
from concurrent.futures import ThreadPoolExecutor

# This simulation is designed to test multiple scans simultaneously (an unrealistic scenario). The code is used to simulate multiple concurrent requests to test the rate limiter.


def generate_user_data():

    for i in range(1, 25):
        yield i, f"test{i}"

def post_request(user_id, user_name):
    url = "http://localhost:8080/v1/movement/scan"
    payload = {
        "userId": user_id,
        "userName": user_name
    }
    headers = {
        "Content-Type":"application/json"
    }



    try:
        response = requests.post(url, json=payload, headers=headers)
        print(f"User ID: {user_id}, User Name: {user_name}, Response: {response.status_code}")
    except Exception as e:
        print(f"User ID: {user_id}, User Name: {user_name}, Error: {e}")

if __name__ == "__main__":

    num_calls = 10 # Call Samples number

    with ThreadPoolExecutor(max_workers=num_calls) as executor:
        user_data_generator = generate_user_data()
        executor.map(lambda _: post_request(*next(user_data_generator)), range(num_calls))
