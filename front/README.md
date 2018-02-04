## Front

**build**


```
docker build -t front .

```

**run**

```
docker run -it -v ${PWD}:/usr/src/app -p 3000:3000 --rm front

```
