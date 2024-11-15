# User Manual: Configuration

## Execution

To execute the project, run it with the build script (only in a development context):

```bash
build.sh r
```

For standard execution, execute the command line :

```bash
java -jar target/JavaGameInADay-1.0.2.jar
```

Or for the Linux machine owner or in a Windows Git Bash terminal :

```bash
target/build/JavaGameInADay-1.0.2.run
```

## Configuration

The game configuration is provided through a properties file on the command line :

```bash
target/build/JavaGameInADay-1.0.2.run -config=./my-config.properties
```

Where the `./my-config-properties` file is along the `JavaGameInADay-1.0.2.run` file.

This configuration file contains multiple keys sections. Let's discover those.

### Debug and test `app.debug`

| Configuration Key | Description                                                                                                                                                                      | Default value |
|:------------------|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:--------------|
| `app.debug.level` | define the level of debug output on the console. Value from 0 (No output) to 5(max output) <br/></br>**WARNING** Using level 5 will be highly verbose and can slowdown the game. | 0 (No output) |
| `app.test`        | set to `true` will the game in a test mode preventing from Game looping.                                                                                                         | false         |

### Rendering `app.render`

| Configuration Key         | Description                                                                 | Default value      |
|:--------------------------|:----------------------------------------------------------------------------|:-------------------|
| `app.render.window.title` | Set the window or application title                                         | "Test001 Demo App" |
| `app.render.window.size`  | Define the size of the window. must respect the format "`[width]x[height]`" | 640x400            |
| `app.render.buffer.size`  | the the internal rendering buffer size in pixel: "`[width]x[height]`"       | 320x200            |

### Physic Engine `app.physic`

#### World

| Configuration Key                 | Description                                                                                | Default value |
|:----------------------------------|:-------------------------------------------------------------------------------------------|:--------------|
| `app.physic.world.play.area.size` | Define the play area zone for the game world. must respect the format "`[width]x[height]`" | 800x600       | 

### Scenes `app.scene`

| Configuration Key   | Description                           | Default value |
|:--------------------|:--------------------------------------|:--------------|
| `app.scene.default` | Define the starting 'scene' instance. | play          |