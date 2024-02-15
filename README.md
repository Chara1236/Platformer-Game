# Platformer-Game

### Run game
Open in Java IDE of choice and compile the program named "Main.java" located at ~/src/Main.java



## Platformer (What a creative name!) features:
- 2D scrolling platformer with sprite based graphics
- Customizable keyboard controls
- Pausing mechanics
- Autosave feature
- Throwable bomb mechanics, which includes ability to jump boost off the bombs explosion
- Movable tiles
- Parallax background
- Txt file based level editor
- Actually hard levels!


## Gameplay:
![image](https://github.com/Chara1236/Platformer-Game/assets/53840675/baf2e6bc-1601-46ab-96cf-f9b2eec5118c)

Mostly generic movement keys

---

![image](https://github.com/Chara1236/Platformer-Game/assets/53840675/db79914e-e8ec-4520-9d71-b684aa8a6429)

Customizable binds in settings which is in the main menu

---

![image](https://github.com/Chara1236/Platformer-Game/assets/53840675/bab7c863-8b92-430d-883a-60f0a95fcc7a)

Slime enemies

---

![image](https://github.com/Chara1236/Platformer-Game/assets/53840675/4f2ad23b-9d1f-44d3-9aaf-de5c24bd035b)

Movable tiles

---

![image](https://github.com/Chara1236/Platformer-Game/assets/53840675/e0b3bbe6-520e-42f2-a088-7f8ec0ea4f6f) Aiming bomb with left click

![image](https://github.com/Chara1236/Platformer-Game/assets/53840675/379fd75b-2d69-4c2e-9f49-aed508ed060c) Bomb after landing

![image](https://github.com/Chara1236/Platformer-Game/assets/53840675/9dfbd2e4-506f-4656-a667-9ced2f4ec5c9) Bomb after explosion!

---

More photos
![image](https://github.com/Chara1236/Platformer-Game/assets/53840675/9427da7a-58cb-427f-abbd-2664314b42bf)
![image](https://github.com/Chara1236/Platformer-Game/assets/53840675/b707233e-74a9-4371-82e2-980058e7c218)
![image](https://github.com/Chara1236/Platformer-Game/assets/53840675/98c3d59a-ec10-45f0-b807-eaf27dc27d6a)
![image](https://github.com/Chara1236/Platformer-Game/assets/53840675/6233f1d8-c304-47cf-b2b4-1c9eaad5fd11)


## Level Editors
Create a 3 files in ~/saves/ for a new level x you want to create. Since there are 6 levels in the default game, you would start off with creating Level7.txt, Level7-signs.txt, and Level7-dialogue.txt. These represent the level layout, text, and pre level dialogue.

### Levelx.txt
Character codes
```
 Grass Tiling:
    qwe
    asd
    zxc
EG. q would be a dirt tile with grass on the top left corner, and d would be a dirt tile with grass on the right edge
!: Slime
v= background wall
o= steel (movable tile)
u= shooter(Up)
h= shooter(Left)
k= shooter(Right)
m= shooter(Down)
b= wooden box
l= lava
+= finish line
```
![image](https://github.com/Chara1236/Platformer-Game/assets/53840675/089a6745-9585-45a1-b4ab-4beb3c280215)


### Levelx-signs.txt
Format
```
//Bolded text
x-cord y-cord text
...
...
/
//Unbolded text
x-cord y-cord text
...
...
```
![image](https://github.com/Chara1236/Platformer-Game/assets/53840675/af022a44-5784-4069-ae6e-1ff33293b62e)


### Levelx-dialogue.txt
Format
```
Text
...
...
```
![image](https://github.com/Chara1236/Platformer-Game/assets/53840675/8c26c88a-2f93-4415-89ba-b32e0a49bcfd)



