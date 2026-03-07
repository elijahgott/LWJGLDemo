import utils.Timer;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.ARBVertexArrayObject.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15C.glBindBuffer;
import static org.lwjgl.opengl.GL15C.glBufferData;
import static org.lwjgl.opengl.GL15C.glGenBuffers;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main {
    final static String windowTitle = "Title";
    public long window;

    public int width = 640;
    public int height = 480;

    float red = 0.0F;
    float green = 0.0F;
    float blue = 0.0F;

    int shaderProgram;
    int vertexShader;
    int fragmentShader;

    int vao;
    int vbo;

    private static final float[] vertices = {
            -0.5F, -0.5F, 0F,
            0.5F, -0.5F, 0F,
            0F, 0.5F, 0F
    };

    // vertex shader
    String vertexShaderSource = "#version 330 core\n" +
            "layout (location = 0) in vec3 aPos;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position = vec4(aPos.x, aPos.y, aPos.z, 1.0);\n" +
            "}\0";

    // fragment shader
    String fragmentShaderSource = "#version 330 core\n" +
            "out vec4 FragColor;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    FragColor = vec4(1.0f, 0.5f, 0.2f, 1.0f);\n" +
            "}\0";

    public boolean running = false;
    public Timer timer = new Timer();

    public void startGame(){
        init();

        gameLoop();

        // disposes of all resources
        if(!running){
            dispose();
        }
    }

    public void gameLoop(){
        while(running){
            // input();
            // update();
             render();

//            sleep(sleepTime);
        }
    }

    public void render(){
        float delta;

        // RENDER LOOP
        while(!glfwWindowShouldClose(window)){
            // clear screen so it can be redrawn
            glClearColor(red, green, blue, 1F);
            glClear(GL_COLOR_BUFFER_BIT);

            // use shader program
            glUseProgram(shaderProgram);
            glBindVertexArray(vao);
            glDrawArrays(GL_TRIANGLES, 0, 3);

            // swap buffers
            // double buffers, one is shown on screen and one is the next frame
            glfwSwapBuffers(window);
            glfwSwapInterval(1);

            glfwPollEvents();

            delta = timer.getDelta();

            // update(delta);
            timer.updateUPS();

            // render();
            timer.updateFPS();

            //update timer
            timer.update();

            // print FPS and UPS
//            System.out.println("FPS: " + timer.getFPS() + " --- UPS: " + timer.getUPS());
        }

        running = false;
    }

    public void init(){
        // initialize timer
        timer.init();

        // INITIALIZE WINDOW

        // set error callback
        glfwSetErrorCallback(errorCallback);

        // initialize glfw
        if(!glfwInit()){
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        // create window
        window = glfwCreateWindow(width, height, windowTitle, NULL, NULL);
        running = true;

        if(window == NULL){
            running = false;
            glfwTerminate();
            throw new RuntimeException("Failed to create the GLFW window");
        }
        // set resize listener
        glfwSetFramebufferSizeCallback(window, framebufferSizeCallback);

        // set key listener
        glfwSetKeyCallback(window, keyCallback);

        glfwMakeContextCurrent(window);

        // initialize openGL (?)
        GL.createCapabilities();

        // create viewport to render (0, 0 is bottom left corner)
        glViewport(0, 0, width, height);

//        System.out.println("OpenGL Version: " + glGetString(GL_VERSION));
        // SHADERS !!!

        // vertex array object
        vao = glGenVertexArrays();
        // vertex buffer object
        vbo = glGenBuffers();
        setUpVertexData(vao, vbo);

        // VERTEX SHADER
        vertexShader = glCreateShader(GL_VERTEX_SHADER);
        // FRAGMENT SHADER
        fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        // SHADER PROGRAM
        shaderProgram = glCreateProgram();

        // set vertex shader source
        glShaderSource(vertexShader, vertexShaderSource);
        glCompileShader(vertexShader);

        // set fragment shader source
        glShaderSource(fragmentShader, fragmentShaderSource);
        glCompileShader(fragmentShader);

        // link shaders to shader program
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);

        // delete shaders
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        // LINKING VERTEX ATTRIBUTES
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.SIZE, 0);
        glEnableVertexAttribArray(0);

        // unbind VAO
        glBindVertexArray(0);
    }

    public void dispose(){


        glDeleteVertexArrays(1);
        glDeleteBuffers(1);
        glDeleteProgram(shaderProgram);

        // after window is terminated
        glfwDestroyWindow(window);
        keyCallback.free();

        // after destroying window
        glfwTerminate();
        errorCallback.free();
    }

    // CALLBACKS
    private final GLFWErrorCallback errorCallback = GLFWErrorCallback.createPrint(System.err);

    private final GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            if(key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true);
            }
            else {
                float stepSize = 0.05F;
                // change red
                if (key == GLFW_KEY_Q) {
                    red += stepSize;
                    if (red >= 1.0F) {
                        red = 1.0F;
                    }
                }
                if (key == GLFW_KEY_A) {
                    red -= stepSize;
                    if (red <= 0.0F) {
                        red = 0.0F;
                    }
                }

                // change green
                if (key == GLFW_KEY_W) {
                    green += stepSize;
                    if (green >= 1.0F) {
                        green = 1.0F;
                    }
                }
                if (key == GLFW_KEY_S) {
                    green -= stepSize;
                    if (green <= 0.0F) {
                        green = 0.0F;
                    }
                }

                // change blue
                if (key == GLFW_KEY_E) {
                    blue += stepSize;
                    if (blue >= 1.0F) {
                        blue = 1.0F;
                    }
                }
                if (key == GLFW_KEY_D) {
                    blue -= stepSize;
                    if (blue <= 0.0F) {
                        blue = 0.0F;
                    }
                }
            }
        }
    };

    private final GLFWFramebufferSizeCallback framebufferSizeCallback = new GLFWFramebufferSizeCallback() {
        @Override
        public void invoke(long window, int width, int height) {
            glViewport(0, 0, width, height);
        }
    };

    private static void setUpVertexData(int vao, int vbo) {
        // Bind the Vertex Array Object first, then bind and set vertex buffer(s), and then configure vertex attributes(s).
        glBindVertexArray(vao);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, NULL);
        glEnableVertexAttribArray(0);

        // note that this is allowed, the call to glVertexAttribPointer registered VBO as the vertex attribute's bound vertex buffer object so afterwards we can safely unbind
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        // You can unbind the VAO afterwards so other VAO calls won't accidentally modify this VAO, but this rarely happens. Modifying other
        // VAOs requires a call to glBindVertexArray anyways so we generally don't unbind VAOs (nor VBOs) when it's not directly necessary.
        glBindVertexArray(0);
    }

    public static void main(String[] args) {
        new Main().startGame();
    }
}