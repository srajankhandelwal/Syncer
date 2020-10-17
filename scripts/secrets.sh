mkdir "app/keystores"
echo "$KEY_STORE" | base64 --decode >> ./app/keystores/todoapp.keystore
