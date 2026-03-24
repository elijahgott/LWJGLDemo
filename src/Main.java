import org.lwjgl.system.MemoryStack;
import utils.Timer;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import java.awt.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

// LWJGL
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.ARBVertexArrayObject.*;

// OpenGL
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15C.glBindBuffer;
import static org.lwjgl.opengl.GL15C.glBufferData;
import static org.lwjgl.opengl.GL15C.glGenBuffers;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.system.MemoryUtil.*;

// GLM - openGL Math
import org.joml.Matrix4f;
import org.joml.Vector3f;

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
    int ebo;

    private static final float[] vertices = {
            // positions        // texture coords
            -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,
            0.5f, -0.5f, -0.5f,  1.0f, 0.0f,
            0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
            0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
            -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
            -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,

            -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
            0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
            0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
            0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
            -0.5f,  0.5f,  0.5f,  0.0f, 1.0f,
            -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,

            -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
            -0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
            -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
            -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
            -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
            -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

            0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
            0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
            0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
            0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
            0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
            0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

            -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
            0.5f, -0.5f, -0.5f,  1.0f, 1.0f,
            0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
            0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
            -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
            -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,

            -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
            0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
            0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
            0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
            -0.5f,  0.5f,  0.5f,  0.0f, 0.0f,
            -0.5f,  0.5f, -0.5f,  0.0f, 1.0f
    };

    private static final int[] indices = {
            0, 1, 3, // first triangle
            1, 2, 3 // second triangle
    };

    private static final Vector3f[] cubePositons = new Vector3f[] {
            new Vector3f(-1.5f, -0.5f, -0.5f),
            new Vector3f(-0.5f, 0.5f, -1.0f),
            new Vector3f(0.5f, -0.5f, -0.5f),
            new Vector3f(0.75f, 0.5f, 0.0f),
    };

    // shader sources
    String vertexShaderSource = "";
    String fragmentShaderSource = "";

    // textures
    int texture1;
    int texture2;

    public boolean running = false;

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

    public void init(){
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

        // TEXTURES !!
        texture1 = loadTexture("res/textures/elo.jpg");
        texture2 = loadTexture("res/textures/goggins.jpg");

        // SHADERS !!!
        // get fragment shaders from file
        try{
            fragmentShaderSource = Files.readString(Path.of("src/shaders/fragmentShader.txt"));
        }
        catch (IOException e){
            e.printStackTrace();
        }

        // get vertex shaders from file
        try{
            vertexShaderSource = Files.readString(Path.of("src/shaders/vertexShader.txt"));
        }
        catch (IOException e){
            e.printStackTrace();
        }

        // vertex array object
        vao = glGenVertexArrays();
        // vertex buffer object
        vbo = glGenBuffers();
        // element buffer object
        ebo = glGenBuffers();

        setUpVertexData(vao, vbo, ebo);

        // VERTEX SHADER
        final int vertexShader = createShader(GL_VERTEX_SHADER, vertexShaderSource);
        // FRAGMENT SHADER
        final int fragmentShader = createShader(GL_FRAGMENT_SHADER, fragmentShaderSource);
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

        // unbind VAO
        glBindVertexArray(0);
    }

    public void render(){
        // use shader program
        glUseProgram(shaderProgram);

        // enable depth test for z index
        glEnable(GL_DEPTH_TEST);

        // RENDER LOOP
        while(!glfwWindowShouldClose(window)){
            glfwPollEvents();

            // clear screen so it can be redrawn
            glClearColor(red, green, blue, 1F);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // bind texture
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, texture1);

            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, texture2);

            int text1loc = glGetUniformLocation(shaderProgram, "texture1");
            glUniform1i(text1loc, 0);

            int text2loc = glGetUniformLocation(shaderProgram, "texture2");
            glUniform1i(text2loc, 1);

            // model, view, and projection matrices
            Matrix4f model = new Matrix4f();
            Matrix4f view = new Matrix4f();
            Matrix4f projection = new Matrix4f();

            model.rotate((float)glfwGetTime() * (float)Math.toRadians(-50.0), new Vector3f(0.5F, 0.5F, 0.5F).normalize());
            view.translate(0.0F, 0.0F, -3.0F); // slide camera back
            projection.perspective((float)Math.toRadians(45.0), (float)width/(float)height, 0.1F, 100.0F);

            // matrix uniforms
            int modelLocation = glGetUniformLocation(shaderProgram, "model");
            int viewLocation = glGetUniformLocation(shaderProgram, "view");
            int projectionLocation = glGetUniformLocation(shaderProgram, "projection");

            try(MemoryStack stack = MemoryStack.stackPush()) {
                FloatBuffer fb = stack.mallocFloat(16);

                glUniformMatrix4fv(modelLocation, false, model.get(fb));
                glUniformMatrix4fv(viewLocation, false, view.get(fb));
                glUniformMatrix4fv(projectionLocation, false, projection.get(fb));
            }

            // RENDER CONTAINER
            glBindVertexArray(vao);

            for(int i = 0; i < cubePositons.length; i++){
                model = new Matrix4f();
                model.translate(cubePositons[i]);
                float angle = 20.0F * i;
                model.rotate((float)glfwGetTime() * (float)Math.toRadians(angle), new Vector3f(0.5F, 0.5F, 0.5F));
                try(MemoryStack stack = MemoryStack.stackPush()) {
                    FloatBuffer fb = stack.mallocFloat(16);

                    glUniformMatrix4fv(modelLocation, false, model.get(fb));
                }

                glDrawArrays(GL_TRIANGLES, 0, 36);
            }

            // swap buffers
            // double buffers, one is shown on screen and one is the next frame
            glfwSwapBuffers(window);
            glfwSwapInterval(1);
        }

        running = false;
    }

    public void dispose(){
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);
        glDeleteTextures(texture1);
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

    private static int createShader(int type, String shaderSource) {
        final int shader = glCreateShader(type);

        glShaderSource(shader, shaderSource);
        glCompileShader(shader);

        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer success = stack.mallocInt(1);

            glGetShaderiv(shader, GL_COMPILE_STATUS, success);

            if(success.get(0) == GL_FALSE){
                final String infoLog = glGetShaderInfoLog(shader);
                System.err.println("Shader compilation failed: " + infoLog);
            }
        }

        return shader;
    }

    private static void setUpVertexData(int vao, int vbo, int ebo) {
        // Bind the Vertex Array Object first, then bind and set vertex buffer(s), and then configure vertex attributes(s).
        glBindVertexArray(vao);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        // position attribute
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        // color attribute -- NOT IN SHADER RN
//        glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * Float.BYTES, 3 * Float.BYTES);
//        glEnableVertexAttribArray(1);

        // texture attribute
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // note that this is allowed, the call to glVertexAttribPointer registered VBO as the vertex attribute's bound vertex buffer object so afterwards we can safely unbind
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        // You can unbind the VAO afterwards so other VAO calls won't accidentally modify this VAO, but this rarely happens. Modifying other
        // VAOs requires a call to glBindVertexArray anyways so we generally don't unbind VAOs (nor VBOs) when it's not directly necessary.
        glBindVertexArray(0);
    }

    private static int loadTexture(String path){
        final int texture = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, texture);

        // set texture wrapping parameters
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        // set texture filtering parameters
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        // load image
        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.ints(0);
            IntBuffer height = stack.ints(0);
            IntBuffer nrChannels = stack.ints(0);

            stbi_set_flip_vertically_on_load(true);
            ByteBuffer data = stbi_load(path, width, height, nrChannels, 0);

            if(data != null){
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0), 0, GL_RGB, GL_UNSIGNED_BYTE, data);
                glGenerateMipmap(GL_TEXTURE_2D);
            }
            else{
                System.err.println("Could not load texture! " + path);
            }

            stbi_image_free(data);
        }

        return texture;
    }

    public static void main(String[] args) {
        new Main().startGame();
    }
}