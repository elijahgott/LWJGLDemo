import org.lwjgl.system.MemoryStack;
import utils.Timer;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import java.awt.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.ARBVertexArrayObject.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15C.glBindBuffer;
import static org.lwjgl.opengl.GL15C.glBufferData;
import static org.lwjgl.opengl.GL15C.glGenBuffers;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
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
    int ebo;

    private static final float[] vertices = {
            // positions          // colors           // texture coords
            0.5f,  0.5f, 0.0f,   1.0f, 0.0f, 0.0f,   1.0f, 1.0f, // top right
            0.5f, -0.5f, 0.0f,   0.0f, 1.0f, 0.0f,   1.0f, 0.0f, // bottom right
            -0.5f, -0.5f, 0.0f,   0.0f, 0.0f, 1.0f,   0.0f, 0.0f, // bottom left
            -0.5f,  0.5f, 0.0f,   1.0f, 1.0f, 0.0f,   0.0f, 1.0f  // top left
    };

    private static final int[] indices = {
            0, 1, 3, // first triangle
            1, 2, 3 // second triangle
    };

    // vertex shader
    String vertexShaderSource = "#version 330 core\n" +
            "layout (location = 0) in vec3 aPos;\n" +
            "layout (location = 1) in vec3 aColor;\n" +
            "layout (location = 2) in vec2 aTexCoord;\n" +
            "\n" +
            "out vec3 ourColor;\n" +
            "out vec2 texCoord;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position = vec4(aPos, 1.0F);\n" +
            "    ourColor = aColor;\n" +
            "    texCoord = vec2(aTexCoord.x, aTexCoord.y);\n" +
            "}";

    // fragment shader
    String fragmentShaderSource = "#version 330 core\n" +
            "out vec4 FragColor;\n" +
            "\n" +
            "in vec3 ourColor;\n" +
            "in vec2 texCoord;\n" +
            "\n" +
            "// texture sampler\n" +
            "uniform sampler2D texture1;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    FragColor = texture(texture1, texCoord);\n" +
            "}";

    // load texture
    int texture1;
    int texture2;

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
            glfwPollEvents();

            // clear screen so it can be redrawn
            glClearColor(red, green, blue, 1F);
            glClear(GL_COLOR_BUFFER_BIT);

            // bind texture
            glBindTexture(GL_TEXTURE_2D, texture1);

            // use shader program
            glUseProgram(shaderProgram);

            // UNIFORM (GLOBAL)
//            // change triangle color over time
//            double timeValue = glfwGetTime();
//            double colorValue = (Math.sin(timeValue) / 2.0) + 0.5;
//            int vertexColorLocation = glGetUniformLocation(shaderProgram, "ourColor");
//            glUniform4f(vertexColorLocation, 0.0F, (float)colorValue, 0.0F, 1.0F);

            // render triangle
            glBindVertexArray(vao);
            glDrawArrays(GL_TRIANGLES, 0, 3);
//            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0); // this crashes my program??

            // swap buffers
            // double buffers, one is shown on screen and one is the next frame
            glfwSwapBuffers(window);
            glfwSwapInterval(1);


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

        // TEXTURES !!
        texture1 = loadTexture("res/textures/elo.jpg");

        // SHADERS !!!

        // vertex array object
        vao = glGenVertexArrays();
        // vertex buffer object
        vbo = glGenBuffers();
        // ebo??
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
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * Float.BYTES, NULL);
        glEnableVertexAttribArray(0);

        // color attribute
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        // texture attribute
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * Float.BYTES, 6 * Float.BYTES);
        glEnableVertexAttribArray(2);

        glBindBuffer(GL_ARRAY_BUFFER, ebo);
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