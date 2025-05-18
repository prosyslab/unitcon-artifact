1. Pull the pre-built Docker image from Dockerhub.
    ```console
    $ docker pull prosyslab/unitcon-artifact
    $ docker pull prosyslab/unitcon-artifact-utbot
    ```

2. Start Docker containers
    ```console
    $ docker run -dit --name unitcon prosyslab/unitcon-artifact bash
    $ docker run -dit --name utbot prosyslab/unitcon-artifact-utbot bash
    ```

3. Check validity
    1. Check if the `unitcon` container is built well.
        ```console
        $ docker exec -it unitcon bash
        root:~/unitcon# ocaml --version
        The OCaml toplevel, version 4.13.1
        root:~/unitcon# java -version
        openjdk version "1.8.0_442"
        OpenJDK Runtime Environment (build 1.8.0_442-8u442-b06~us1-0ubuntu1~22.04-b06)
        root:~/unitcon# python3 --version
        Python 3.10.12
        ```

    2. Check if the `utbot` container is built well.
        ```console
        $ docker exec -it utbot bash
        root:/usr/src/baselines# ls -l /usr/lib/jvm/
        total 16
        lrwxrwxrwx 1 root root   21 Jan 23 08:11 java-1.17.0-openjdk-amd64 -> java-17-openjdk-amd64
        lrwxrwxrwx 1 root root   20 Jan 26 16:38 java-1.8.0-openjdk-amd64 -> java-8-openjdk-amd64
        drwxr-xr-x 9 root root 4096 Apr 12 16:57 java-17-openjdk-amd64
        drwxr-xr-x 7 root root 4096 Apr 12 16:57 java-8-openjdk-amd64
        drwxr-xr-x 2 root root 4096 Apr 12 16:57 openjdk-17
        lrwxrwxrwx 1 root root   15 Dec 19  2022 zulu11 -> zulu11-ca-amd64
        drwxr-xr-x 9 root root 4096 Dec 19  2022 zulu11-ca-amd64
        ```