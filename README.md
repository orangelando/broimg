# Photo duplicate finder

Project that finds exact matches and similar images in a dir.

## Usage

### Prerequisites

Maven is required to build a fat jar using the "mvn package" goal.

### Similarities not handled

Rotations, mirror flips and crops not handled. The similar search is done using an 8x8 dhash and searching
for images with a hamming distance of less 2.


