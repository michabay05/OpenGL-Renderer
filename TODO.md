# TODO LIST

## General
- [ ] Add comments to further describe the functionality codes
- [ ] Remove the `GLDraw` interface defined in the renderer
- [ ] Add Textures loading and rendering

## Circle
- [ ] Find a way to calculate to automatically calculate an optimal number of segments
    - The calculations of number of segments should be proportional to the radius of the circle

## Shader
- [ ] BUG: The color from the vertex buffer object isn't being passed onto the fragment shader
- [ ] Add shader reloading feature
- [ ] REFACTOR: using `IntBuffer`s instead of an `int` array to pass a `int`(primitive) by reference  
