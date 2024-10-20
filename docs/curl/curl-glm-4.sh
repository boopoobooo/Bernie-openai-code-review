curl -X POST \
        -H "Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsInNpZ25fdHlwZSI6IlNJR04ifQ.eyJhcGlfa2V5IjoiOTc4ZjA1NWZiNjA5NGUxNjg2M2E1OTA0YmU1ZWFkY2YiLCJleHAiOjE3MjkzNDIzODg1OTksInRpbWVzdGFtcCI6MTcyOTM0MDU4ODYwNX0.ua0rBBaXsyHGzN6rKe-zQFZKGSwiPSMo1vNmO0_5BX0" \
        -H "Content-Type: application/json" \
        -H "User-Agent: Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)" \
        -d '{
          "model":"glm-4-plus",
          "stream": "true",
          "messages": [
              {
                  "role": "user",
                  "content": "1+1"
              }
          ]
        }' \
  https://open.bigmodel.cn/api/paas/v4/chat/completions