# Peer-to-Peer-P2P-image-sharing-system
It is a project which shows my skills on writing networking and multi-threading program in Java. 
You need the file User.txt and put it in the Java project's root folder. 
You can have multiple users, load a new image in the server, which will update all peers' GUI. 
You can also swap blocks in the server GUI and this update will be immediately shown in all peers' GUI.

First, you need to choose an image file in the JFileChooser. 
Second, you can load a new image by pressing the button "Load another image".
Then run the ImagePeer.java. Enter the server’s IP address, the username and password in User.txt. 
The passwords will be hashed and sent to the server with the IP address and the username.
You can have as many peers as you like. 
They will load the image by getting the separated image blocks from the previous users.
When you load a new image in the server GUI, all peers will also have the new image shown.
When you swapped two blocks of the image by dragging and dropping them, all peers will also have the blocks swapped.
