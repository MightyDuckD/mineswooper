current pipeline

#pre (color image)
gaussian blur
canny edge

#main (black and white edges)
countour detection
 -> get bounding
 -> find all which could match a minesweeper stone (e.g 40x40 pixel)
 
#post (list of boxes)
find grid
 -> write all x into list
 -> apply Kernel density estimation
 -> find local maxima
