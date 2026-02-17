#!/bin/bash

BASE_URL="http://localhost:8081"
USER_ID="user123"
VEHICLE_ID="vT1"

echo "=== 1. Health Check ==="
curl -s -X GET "$BASE_URL/health" | python3 -m json.tool

echo -e "\n=== 2. Register User ==="
curl -s -X POST "$BASE_URL/register" \
  -H "Content-Type: application/json" \
  -d '{
    "id": "'"$USER_ID"'",
    "username": "GonzaloTest",
    "email": "gonzalo@test.com",
    "password": "securepass",
    "description": "Tester",
    "phone": "600123456",
    "role": "ADMIN"
}' | python3 -m json.tool

echo -e "\n=== 3. Login User ==="
LOGIN_RESP=$(curl -v -X POST "$BASE_URL/login" \
  -H "Content-Type: application/json" \
  -d '{"email": "gonzalo@test.com", "password": "securepass"}')
echo "$LOGIN_RESP"

echo -e "\n=== 4. Insert Vehicle for User ==="
curl -s -X POST "$BASE_URL/users/$USER_ID/vehicles" \
  -H "Content-Type: application/json" \
  -d '{
    "id": "'"$VEHICLE_ID"'",
    "marca": "Tesla",
    "modelo": "Model 3",
    "año": 2024,
    "precio": 45000.0,
    "kilometros": 100,
    "potencia": 300,
    "userId": "'"$USER_ID"'"
}'

echo -e "\n=== 5. List Vehicles for User ==="
curl -s -X GET "$BASE_URL/users/$USER_ID/vehicles" | python3 -m json.tool

echo -e "\n=== 6. Get Specific Vehicle ==="
curl -s -X GET "$BASE_URL/users/$USER_ID/vehicles/$VEHICLE_ID" | python3 -m json.tool

echo -e "\n=== 7. Update Vehicle ==="
curl -s -X PUT "$BASE_URL/users/$USER_ID/vehicles/$VEHICLE_ID" \
  -H "Content-Type: application/json" \
  -d '{"precio": 42000.0}'

echo -e "\n=== 8. Verify Update ==="
curl -s -X GET "$BASE_URL/users/$USER_ID/vehicles/$VEHICLE_ID" | python3 -m json.tool

echo -e "\n=== 9. Delete Vehicle ==="
curl -s -X DELETE "$BASE_URL/users/$USER_ID/vehicles/$VEHICLE_ID"

echo -e "\n=== 10. Verify Deletion ==="
curl -s -X GET "$BASE_URL/users/$USER_ID/vehicles/$VEHICLE_ID"

echo -e "\n=== 11. Clean Up (Delete User) ==="
# This should also delete leftover vehicles if any
curl -s -X DELETE "$BASE_URL/users/$USER_ID"

echo -e "\nDone."
