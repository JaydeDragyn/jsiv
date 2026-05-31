Copyright (c) 2026 Jayde Dragyn \
Licensed under the MIT License. \
See LICENSE.MD file in the project root for full license information. \
See LICENSE-3RD-PARTY.txt in the project root for 3rd party license information.



###                    JSIV - Jayde's Simple Image Viewer
###                             by Jayde Dragyn
(jaydedragyn@outlook.com)

A simple image viewer with only the features I wanted.

I was trying to view assets to use in another project - small images that are
all 16x16 or 32x32 pixels.  I wanted to magnify them to get a better look, 
but with the image viewers I had available I had to open the next file, 
scroll to zoom in and then repeat.  That scroll to zoom in was annoying and 
a distraction, even if I could use the left and right arrow keys to quickly 
open the next file.

So when you open an image with this viewer, it will zoom in or out on the
image automatically so that it is as large as it can get in the program window.
This way you can quickly scan a folder full of images without having to zoom
in each time a new image opens.

Additional features include:
- Quick open next and open previous image
- Manual magnification (zoom in and out as you please)
- Pan image around at any magnification
- Change background color
- Image size information
- Window size information
- Pixel color display

---

I used Java's built-in ImageIO library for loading and displaying images,
except for WebP images, which uses the TwelveMonkeys ImageIO library
(which is available at https://github.com/haraldk/TwelveMonkeys).
So while I know image formats and file IO, I let the existing facilities
and 3rd party library handle that heavy lifting.

---

Written in Java 17 (OpenJDK 17.0.4.1 2022-08-18LTS)
Everything was written using the Kate editor and a git bash shell.
The images used in the program are generated, no external assets.
The user manual is a manually formatted .html file I created in Kate.

I asked ChatGPT 5.5 (OpenAI), Claude Sonnet 4.6 (Anthropic) and
Gemini Flash 3.5 (Google) for code reviews and guidance on some 
parts, but all code was written by me, no vibe coding.

TwelveMonkeys modules used (and included in the libs folder in the JAR):
    common-image-3.13.1
    common-io-3.13.1
    common-lang-3.13.1
    imageio-core-3.13.1
    imageio-metadata-3.13.1
    imageio-webp-3.13.1

---

Since I developed this in a Git BASH shell, I wrote a couple of shell
scripts to make my life a little easier.

### make.sh 
This will delete any old .class and .jar files and then compile and create
an executable JAR in the main project directory.

The compiled .class files will go into the jar/ directory.

If you want to compile this manually, the command line from the project's
root directory would be:

     javac -d jar/ -cp src/ src/*.java

### jsiv.sh
This will just run the jar file with:

     java -jar jsiv

---

There is a User Manual available from the Help menu (or by pressing F1)
in program that will explain how to use the program and what does what.

If you find any issues or have suggestions to improve the program, please
feel free to let me know at:

    JaydeDragyn@outlook.com
    
with JSIV on the subject line.

---

### Future Plans

This program was created for two reasons:

1. 
I am writing another program and got hung up on the program structure.
This program's structure is what I will use in the other program, along
with a lot of newly learned knowledge on many Java Swing idoms.  

Note that I specifically chose to use Java Swing for this program because
it comes with OpenJDK's Java 17 JDK.  JavaFX is not included, and I did
not want to learn a whole new system for this.

2. 
I actually wanted to write this utility to help me with my other program.
Specifically, looking through icons and tile images en masse.  



I will revisit this for two updates:

1. 
I want to enable this to open .webp files, which Java does not natively 
support.  I wanted to get this working before I explored .webp handling
modules.

2. 
I would like to make this accept command line arguments to open files so
a user can associate it with images and just double-click the image to
open it in this like any other image viewer.  I have looked into this 
a bit, and I already understand how command line arguments are received,
I just did not design this version with command line arguments in mind
(I didn't think of it until late in development and already feature-
locked so I could actually finish it).
