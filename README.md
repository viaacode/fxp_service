# fxp_service
Microservice that transfers a file between two servers, triggered by RabbitMQ messages


# Example request

```
{
   "source_host": "test.ftp.com",
   "source_user": "username",
   "source_password": "password",
   "source_file": "original.txt",
   "source_path": "/home/folder/",
   "destination_host": "other.ftp.com",
   "destination_user": "username",
   "destination_password": "password",
   "destination_file": "file.txt",
   "destination_path": "/home/test/"
}
```