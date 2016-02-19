[![Build Status](https://travis-ci.org/dermoritz/imageservice.svg?branch=develop)](https://travis-ci.org/dermoritz/imageservice)

(This readme is work in progress)

# What is imageservice?

Imageservice is meant as a private service that provides access to your images. Access only means: viewing them. I use it to view my family fotos from everywhere.

# How to use

## Get started

You need Java 8 to be installed. Download and uncompress imageService-distribution.zip. Start the contained jar with:

```
java -jar imageService-<version>.jar <folder with your images> <port>
```

Open your Browser and try:
```
localhost:port/next
```

The access can be protected with basic auth. As default the user: "user1:awdrg" has access. but you could change the file users.txt to change or add users (each line one user). If you delete the file the access isn't protected anymore.

You can provide multiple folders separated by `;`. Each folder will be scanned recursively and every file ending with .jpg will be provided via the service.

## Provided endpoints

all relative to &lt;your machine's url : port&gt;

* /next will give you a random image
* /prev .. the last image returned
* /update .. will parse all given folders again - the response is synchron, so it might take some minutes if you parse large directories
* /current .. reloads the current image
* /&lt;filter string&gt; .. returns a random file matching the string (the file path must contain the string)

### additional features

* &lt;filter string&gt;/sort .. returns files matching the filter in order (sorted by path)
* .../auto/&lt;seconds&gt; .. you can add this at the end of every request to get a slide show (your browser is redirected to the same url)

example: `localhost/20151225/sort/auto/10` will give a slide show of all files that contain `20151225` in order.

## logs

The Application will create a "logs" folder with a log file. It will show some details about application start and it will log every access.
