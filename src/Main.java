import Utils.Timer;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main {
    final static String windowTitle = "Title";
    public long window;

    public int width = 640;
    public int height = 480;

    public float red = 0.0F;
    public float green = 0.0F;
    public float blue = 0.0F;

    public boolean running = false;
    public Timer timer = new Timer();

    // fonts
    private Font font;

    // CALLBACKS
    private final GLFWErrorCallback errorCallback = GLFWErrorCallback.createPrint(System.err);

    private final GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            if(key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true);
            }
            else {
                // change red
                if (key == GLFW_KEY_Q) {
                    red += 0.01F;
                    if (red >= 1.0F) {
                        red = 1.0F;
                    }
                }
                if (key == GLFW_KEY_A) {
                    red -= 0.01F;
                    if (red <= 0.0F) {
                        red = 0.0F;
                    }
                }

                // change green
                if (key == GLFW_KEY_W) {
                    green += 0.01F;
                    if (green >= 1.0F) {
                        green = 1.0F;
                    }
                }
                if (key == GLFW_KEY_S) {
                    green -= 0.01F;
                    if (green <= 0.0F) {
                        green = 0.0F;
                    }
                }

                // change blue
                if (key == GLFW_KEY_E) {
                    blue += 0.01F;
                    if (blue >= 1.0F) {
                        blue = 1.0F;
                    }
                }
                if (key == GLFW_KEY_D) {
                    blue -= 0.01F;
                    if (blue <= 0.0F) {
                        blue = 0.0F;
                    }
                }
            }
        }
    };

    public void startGame(){
        init();
        gameLoop();
        if(!running){
            dispose();
        }
    }

    public void gameLoop(){
        GL.createCapabilities();

        while(running){
            // input();
            // update();
             render();

//            sleep(sleepTime);
        }
    }

    public void render(){
        float delta;

        // RENDERING LOOP
        while(!glfwWindowShouldClose(window)){
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

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

            glClearColor(red, green, blue, 1F);

            // print FPS and UPS
            System.out.println("FPS: " + timer.getFPS() + " --- UPS: " + timer.getUPS());
        }

        running = false;
    }

    public void init(){
        // get fonts
        try{
            InputStream is = getClass().getResourceAsStream("/fonts/Silkscreen_Regular.ttf");
            font = Font.createFont(Font.TRUETYPE_FONT, is);
        }
        catch (FontFormatException | IOException e){
            System.err.println(e);
        }

        // initialize timer
        timer.init();

        // INITIALIZE WINDOW

        // set error callback
        glfwSetErrorCallback(errorCallback);

        // initialize glfw
        if(!glfwInit()){
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // create window
        window = glfwCreateWindow(width, height, windowTitle, NULL, NULL);
        running = true;

        if(window == NULL){
            running = false;
            glfwTerminate();
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // set key listener
        glfwSetKeyCallback(window, keyCallback);

        glfwMakeContextCurrent(window);
        GL.createCapabilities();
    }

    public void dispose(){
        // after window is terminated
        glfwDestroyWindow(window);
        keyCallback.free();

        // after destroying window
        glfwTerminate();
        errorCallback.free();
    }

    public static void main(String[] args) {
        new Main().startGame();
    }
}