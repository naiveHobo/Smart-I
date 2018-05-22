# Smart-I

## Introduction
Smart-I is an attempt to harness the power of Artificial Intelligence and Cloud Computing with the aim of helping the visually impaired by narrating the world around them. Using a cascade of deep neural networks, Smart-I is able to predict apt captions for the scene captured in an image and the depth of each pixel in this image with respect to the camera. This information is combined to develop a deep understanding of the environment which is then outputted as speech, thus helping the visually impaired to visualize and navigate in their surroundings.

## Cloud Server
A server is hosted on Microsoft Azure which takes an image from the camera feed of the device, performs inference on the cloud using our deep neural networks and responds with the prediction.

*NOTE: Our current subscription on Azure (Azure Free Pass) forbids us from setting up virtual machines with GPU instances. Hosting the server on a relatively less powerful machine has caused the response time for the server to increase significantly. Smart-I can run in real-time provided the server is set up on a powerful virtual machine on the cloud and this would be taken care of in the next update.*

## Instructions
Just download the apk and install it on your Android device, giving the apl the necessary permissions. Make sure you have an active internet connection. Due to the slow response time of the server, you will have to wait about 15 seconds to get a prediction. As soon as you recieve a respinse from the server, Smart-I will begin narrating the world around you.

## Android Application
The apk for Smart-I is available [here](https://github.com/bennyhawk/CodeFunDo/raw/master/app-release.apk).

Documentation is available [here](https://github.com/bennyhawk/CodeFunDo/raw/master/Smart-I_Doc.pdf).

#### Made with ♥ by:
- Reuben Nellissery 
- Amit Jindal 
- Sarthak Mittal 
