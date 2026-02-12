#!/bin/bash

# Default Base URL
BASE_URL=${BASE_URL:-http://localhost:8081}

# Check for JSON parsing tools (jq or python)
HAS_JQ=false
HAS_PYTHON=false
PYTHON_CMD=""

if command -v jq &> /dev/null; then
    HAS_JQ=true
elif command -v python3 &> /dev/null; then
    HAS_PYTHON=true
    PYTHON_CMD=python3
elif command -v python &> /dev/null; then
    HAS_PYTHON=true
    PYTHON_CMD=python
else
    echo "Error: Neither 'jq' nor 'python' found."
    echo "One of them is required to parse JSON responses."
    exit 1
fi

echo "========================================"
echo "Starting Tests against $BASE_URL"
echo "Using JSON parser: $( [ "$HAS_JQ" = true ] && echo "jq" || echo "$PYTHON_CMD" )"
echo "========================================"

# 1. Health Check
echo ""
echo "[1/7] GET /health"
health_response=$(curl -s -w "\n%{http_code}" "$BASE_URL/health")
health_body=$(echo "$health_response" | head -n -1)
health_status=$(echo "$health_response" | tail -n 1)

if [ "$health_status" -eq 200 ]; then
    echo "SUCCESS: Status 200"
    if echo "$health_body" | grep -q "ok"; then
        echo "Body check passed."
    else
        echo "WARNING: Body did not contain 'ok'"
    fi
else
    echo "FAILURE: Status $health_status"
    exit 1
fi

# 2. Get All Vehicles
echo ""
echo "[2/7] GET /vehicles"
list_status=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/vehicles")
if [ "$list_status" -eq 200 ]; then
    echo "SUCCESS: Status 200"
else
    echo "FAILURE: Status $list_status"
    exit 1
fi

# 3. Create Vehicle (POST)
echo ""
echo "[3/7] POST /vehicles"
create_response=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/vehicles" \
     -H "Content-Type: application/json" \
     -d '{
           "brand": "Ford", 
           "model": "Mustang", 
           "licensePlate": "BASH-007", 
           "createdAt": "2024-01-01"
         }')
create_body=$(echo "$create_response" | head -n -1)
create_status=$(echo "$create_response" | tail -n 1)

if [[ "$create_status" -eq 200 || "$create_status" -eq 201 ]]; then
    echo "SUCCESS: Status $create_status"
else
    echo "FAILURE: Status $create_status"
    echo "Response: $create_body"
    exit 1
fi

# Extract ID logic
VEHICLE_ID=""
if [ "$HAS_JQ" = true ]; then
    VEHICLE_ID=$(echo "$create_body" | jq -r '.id // ._id // .vehicleId // empty')
else
    VEHICLE_ID=$(echo "$create_body" | $PYTHON_CMD -c "
import sys, json
try:
    data = json.load(sys.stdin)
    print(data.get('id') or data.get('_id') or data.get('vehicleId') or '')
except Exception:
    pass
")
fi

if [ -z "$VEHICLE_ID" ]; then
    echo "ERROR: Could not extract vehicle ID from response."
    echo "Response: $create_body"
    exit 1
fi

echo "Captured Vehicle ID: $VEHICLE_ID"

# 4. Get Vehicle By ID
echo ""
echo "[4/7] GET /vehicles/$VEHICLE_ID"
get_status=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/vehicles/$VEHICLE_ID")
if [ "$get_status" -eq 200 ]; then
    echo "SUCCESS: Status 200"
else
    echo "FAILURE: Status $get_status"
    exit 1
fi

# 5. Update Vehicle (PUT)
echo ""
echo "[5/7] PUT /vehicles/$VEHICLE_ID"
update_status=$(curl -s -o /dev/null -w "%{http_code}" -X PUT "$BASE_URL/vehicles/$VEHICLE_ID" \
     -H "Content-Type: application/json" \
     -d '{
           "brand": "Ford Updated", 
           "model": "Mustang GT", 
           "licensePlate": "BASH-007-UPD"
         }')

if [[ "$update_status" -eq 200 || "$update_status" -eq 204 ]]; then
    echo "SUCCESS: Status $update_status"
else
    echo "FAILURE: Status $update_status"
    exit 1
fi

# 6. Delete Vehicle
echo ""
echo "[6/7] DELETE /vehicles/$VEHICLE_ID"
delete_status=$(curl -s -o /dev/null -w "%{http_code}" -X DELETE "$BASE_URL/vehicles/$VEHICLE_ID")

if [[ "$delete_status" -eq 200 || "$delete_status" -eq 204 ]]; then
    echo "SUCCESS: Status $delete_status"
else
    echo "FAILURE: Status $delete_status"
    exit 1
fi

# 7. Verify Delete (GET -> 404)
echo ""
echo "[7/7] Verify Deletion: GET /vehicles/$VEHICLE_ID"
verify_status=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/vehicles/$VEHICLE_ID")

if [ "$verify_status" -eq 404 ]; then
    echo "SUCCESS: Status 404 (Not Found)"
else
    echo "FAILURE: Expected 404, got $verify_status"
    exit 1
fi

echo ""
echo "========================================"
echo "ALL TESTS PASSED"
echo "========================================"
exit 0
