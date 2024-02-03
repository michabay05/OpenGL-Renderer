# TODO LIST

## General
- [ ] ADD: Add more descriptive comments
- [x] REFACTOR: Remove the `GLDraw` interface defined in the renderer
- [ ] ADD: Add Textures loading and rendering

## Circle
- [ ] REFACTOR: Find a way to calculate to automatically calculate an optimal number of segments
    - The calculations of number of segments should be directly proportional to the radius of the circle

## Shader
- [x] BUG: The color from the vertex buffer object isn't being passed onto the fragment shader
- [ ] ADD: shader reloading feature
- [ ] ADD: Create an array of all the uniforms in both shaders
- [ ] REFACTOR: using `IntBuffer`s instead of an `int` array to pass a `int`(primitive) by reference  
- [ ] REFACTOR: repeatedly setting and unsetting the shader can lead to worse performance.
