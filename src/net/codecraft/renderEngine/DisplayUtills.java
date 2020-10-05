package net.codecraft.renderEngine;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GLContext;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.AxisAngle4f;
import org.joml.Math;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import net.codecraft.Entities.Entity;
import net.codecraft.Entities.EntityPlayer;
import net.codecraft.blocks.Block;
import net.codecraft.blocks.BlockGrass;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.EXTFramebufferObject.*;
public class DisplayUtills {
private static float angle;
private static float posZ = 0;
private static float posX = 0;
private static float posY = 0;
private static int colorTextureID;
private static int framebufferID;
private static int depthRenderBufferID;
private static float rotX = 0;
private static float rotY = 0;
private static float rotZ = 0;
private static float realRotX = 0;
private static int BlockPlaceX;
private static int BlockPlaceY;
private static int BlockPlaceZ;
private static Texture dirtTexture;
private static Texture grassTexture;
private static float density = 0.3f;
private static  float vdx = 50;
private static float vdxn = -50;
private static float vdy =25;
private static float vdyn = -25;
private static float vdz = 50;
private static float vdzn = -50;
private static ByteBuffer color= ByteBuffer.allocateDirect(4);
private static EntityPlayer player = new EntityPlayer(0, 3, 0 );

private static float fogColor[] = {1.0f, 1.0f, 1f, 1.0f};
//private static Block Blocks[] = new Block[25*25*25];
private static Chunk chunks[] = new Chunk[3*9*9];
private static Chunk ativeChunks[] = new Chunk[1];
private static FloatBuffer fb = BufferUtils.createFloatBuffer(16);
static Matrix4f m = new Matrix4f();
public static void createDisplay(String version) {
	try {
		Display.setDisplayMode(new DisplayMode(1920, 1080));
		Display.setTitle("Codecraft " + version);
		Display.create();
	} catch (LWJGLException e) {
		e.printStackTrace();
		System.exit(0);
	}
 }
public static void initGL() {
	

	
	
	color.asFloatBuffer().rewind();
	int cx = 0;
	int cy = 0;
	int cz = 0;
	
	for(int i2 = 0; i2 < chunks.length; i2++) {
		Block Blocks[] = new Block[25*25*25];                
		int x = 0;
		int y = 0;
		int z = 0;
	for(int i = 0; i < Blocks.length; i++) {
	
		Blocks[i] = new BlockGrass(x, y, z,Integer.valueOf((z)+String.valueOf(x) + String.valueOf(y)));
		
		
		y++;
	
		if(y >24) {
			y = 0;
			x++;
			
			
		}
		if(x > 24) {
			x = 0;
			z++;
		}
	
        }
	chunks[i2] = new Chunk(Blocks,cx,cy,cz);
	cy++;
	
	if(cy >2) {
		cy = 0;
		cz++;
		
		
	}
	if(cz > 8) {
		cz = 0;
		cx++;
	}

	}
	
	UpdateActiveChunks();
	/*
	GL11.glEnable(GL11.GL_BLEND);
	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	glViewport (0, 0, 1920, 1080);								// Reset The Current Viewport
	glMatrixMode (GL_PROJECTION);								// Select The Projection Matrix
	glLoadIdentity ();											// Reset The Projection Matrix
	GLU.gluPerspective (90.0f, 1920f/1080f, 1.0f, 100.0f);		// Calculate The Aspect Ratio Of The Window	
	glMatrixMode (GL_MODELVIEW);								// Select The Modelview Matrix
	glLoadIdentity ();
*/	
	
	GL11.glEnable(GL11.GL_BLEND);
GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	Matrix4f m = new Matrix4f();
	m.setPerspective((float) Math.toRadians(90.0f), 1920f/1080f, 0.1f, 100.0f);
	glMatrixMode(GL_PROJECTION);
	glFrustum(50,50,35,35,0.1,100);
	glLoadMatrix(m.get(fb));
	m.setLookAt(0.0f, 0.0f, 10.0f,
	            0.0f, 0.0f, 0.0f,
	            0.0f, 1.0f, 0.0f);
	glMatrixMode(GL_MODELVIEW);
	glLoadMatrix(m.get(fb));
	
								// Reset The Modelview Matrix

	
 
	// Start Of User Initialization
	angle		= 0.0f;											// Set Starting Angle To Zero

	glClearColor (0.0f, 0.0f, 0.0f, 0.5f);						// Black Background
	glClearDepth (1.0f);										// Depth Buffer Setup
	glDepthFunc (GL_LEQUAL);									// The Type Of Depth Testing (Less Or Equal)
	glEnable (GL_DEPTH_TEST);									// Enable Depth Testing
	glShadeModel (GL_SMOOTH);									// Select Smooth Shading
	glHint (GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);			// Set Perspective Calculations To Most Accurate
	
	

	// check if GL_EXT_framebuffer_object can be use on this system
	if (!GLContext.getCapabilities().GL_EXT_framebuffer_object) {
		System.out.println("FBO not supported!!!");
		System.exit(0);
	}
	else {
		
		System.out.println("FBO is supported!!!");
		
		// init our fbo

		framebufferID = glGenFramebuffersEXT();											// create a new framebuffer
		colorTextureID = glGenTextures();												// and a new texture used as a color buffer
		depthRenderBufferID = glGenRenderbuffersEXT();									// And finally a new depthbuffer

		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, framebufferID); 						// switch to the new framebuffer

		// initialize color texture
		glBindTexture(GL_TEXTURE_2D, colorTextureID);									// Bind the colorbuffer texture
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);				// make it linear filterd
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, 512, 512, 0,GL_RGBA, GL_INT, (java.nio.ByteBuffer) null);	// Create the texture data
		glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT,GL_COLOR_ATTACHMENT0_EXT,GL_TEXTURE_2D, colorTextureID, 0); // attach it to the framebuffer


		// initialize depth renderbuffer
		glBindRenderbufferEXT(GL_RENDERBUFFER_EXT, depthRenderBufferID);				// bind the depth renderbuffer
		glRenderbufferStorageEXT(GL_RENDERBUFFER_EXT, GL14.GL_DEPTH_COMPONENT24, 512, 512);	// get the data space for it
		glFramebufferRenderbufferEXT(GL_FRAMEBUFFER_EXT,GL_DEPTH_ATTACHMENT_EXT,GL_RENDERBUFFER_EXT, depthRenderBufferID); // bind it to the renderbuffer

		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);									// Swithch back to normal framebuffer rendering
		
	}
	try {
		dirtTexture = TextureLoader.getTexture("JPG", ResourceLoader.getResourceAsStream("Resources/Textures/Blocks/dirt.jpg"));
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}try {
	grassTexture = TextureLoader.getTexture("JPG", ResourceLoader.getResourceAsStream("Resources/Textures/Blocks/grass.jpg"));
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

public static void renderGL() {
	
	if(rotX > 90 && rotX < -90) {
		
	}
	if(rotX > 360|| rotX < -360) {
		rotX = 0;
	}
	if(rotY > 180) {
		rotY = 180;
	}
	if(rotY < -180) {
		rotY = -180;
	}
/*
		if( DisplayUtills.getRotX() < 45 && DisplayUtills.getRotX() > -45) {
			vdx = 30;
			vdxn = -30;
			 vdy = 7;
			 vdyn = -7;
			vdz = 0;
			 vdzn = -256;
			}
		else if( DisplayUtills.getRotX() > 45 && DisplayUtills.getRotX() < 90) {
			vdx = 32;
			vdxn = -5;
			 vdy = 7;
			 vdyn = -7;
			vdz = 30;
			 vdzn = -32;
			}
		else if( DisplayUtills.getRotX() < -45 && DisplayUtills.getRotX() > -90) {
			vdx = 5;
			vdxn = -32;
			 vdy = 7;
			 vdyn = -7;
			vdz = 32;
			 vdzn = -30;
			}else if( DisplayUtills.getRotX() > 90 && DisplayUtills.getRotX() < 135) {
				vdx = 256;
				vdxn = 0;
				 vdy = 7;
				 vdyn = -7;
				vdz = 30;
				 vdzn = -30;
				}
			else if( DisplayUtills.getRotX() < -90 && DisplayUtills.getRotX() >-135) {
				vdx = 0;
				vdxn = -256;
				 vdy = 7;
				 vdyn = -7;
				vdz = 30;
				 vdzn = -30;
				}else if( DisplayUtills.getRotX() > 135 && DisplayUtills.getRotX() < 180) {
					vdx = 32;
					vdxn = -5;
					 vdy = 7;
					 vdyn = -7;
					vdz = 32;
					 vdzn = -30;
					}
				else if( DisplayUtills.getRotX() < -135 && DisplayUtills.getRotX() >-180) {
					vdx = 5;
					vdxn = -32;
					 vdy = 7;
					 vdyn = -7;
					vdz = 30;
					 vdzn = -32;
					 
					}else if( DisplayUtills.getRotX() > 180 && DisplayUtills.getRotX() < 225) {
						vdx = 30;
						vdxn = -30;
						 vdy = 7;
						 vdyn = -7;
						vdz = 256;
						 vdzn = 0;
						 
						}
					else if( DisplayUtills.getRotX() < -180 && DisplayUtills.getRotX() >-225) {
						vdx = 30;
						vdxn = -30;
						 vdy = 7;
						 vdyn = -7;
						vdz = 256;
						 vdzn = 0;
						 
						}
						*/
	if( DisplayUtills.getRotX() < 1) {
		
		
		
	}
	float pRadian  = (float) (rotY * (Math.PI / 180  ));
	realRotX = rotX;
	float yawRadian  = (float) ( (rotX)* (Math.PI / 180));
	// FBO render pass
	int sign =1;
	if(rotY < 0) {
		sign = 1;
	}
	
	glViewport (0, 0, 512, 512);									// set The Current Viewport to the fbo size
	
	glBindTexture(GL_TEXTURE_2D, 0);								// unlink textures because if we dont it all is gonna fail
	glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, framebufferID);		// switch to rendering on our FBO
	
	glClearColor (0.04f, 0.196f, 0.125f, 0.5f);
	glClear (GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);			// Clear Screen And Depth Buffer on the fbo to red
				// draw the box


	// Normal render pass, draw cube with texture

	glViewport (0, 0, 1920, 1080);				// enable texturing
	
	// switch to rendering on the framebuffer
	glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
	glClearColor (0.529f, 0.808f, 0.922f, 0.5f);
	glClear (GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);			// Clear Screen And Depth Buffer on the framebuffer to black
	

	               
	
							
	            
					try {
				 
					
					}catch(Exception e) {
						
					}
				    
									// bind our FBO texture
                     
                   int count = 0;
                 
		        	
                    for(Chunk chunk : chunks) {
                    	if(count == 0) {


    					
					glLoadMatrix(m.get(fb));
					
					 
	                   
	                    
	                    glClearColor (0.529f, 0.808f, 0.922f, 0.5f);
				glRotatef(rotX, 0.0f,1f,0.0f);
				glRotatef((float)(rotY *  Math.cos( yawRadian) * Math.cos(90)) ,1.0f,0.0f,0.0f);
				glRotatef((float)(rotY *  Math.sin( yawRadian) * Math.cos(90) ) ,0.0f,0.0f,1.0f);
					glTranslatef ( posX - 12.5f   ,  posY - 78, posZ - 12.5f);
				
					glRotatef(0,0.0f,1.0f,0.0f);
					glRotatef(0,1.0f,0.0f,0.0f);
					glRotatef(0,0.0f,0.0f,1.0f);
                    	
					
					count++;
                    	}
					
					 int BX = 0;
					   	int BZ = 0;
					   if(rotX < 45 && rotX > -45 && rotY < 45 && rotY > -45) {
						   	 BX = 0;
						    BZ = -4;
					   }
					   else if(rotX > 45 && rotX < 90 && rotY < 45 && rotY > -45) {
						   BX = 2;
						   	BZ = -2;
					   }  else if(rotX < -45 && rotX > -90 && rotY < 45 && rotY > -45) {
						   BX = -2;
						   	BZ = -2;
					   }else if(rotX > 90 && rotX < 135 && rotY < 45 && rotY > -45) {
						   BX = 4;
						   	BZ = 0;
					   }else if(rotX < -90 && rotX > -135 && rotY < 45 && rotY > -45) {
						   BX = -4;
						   	BZ = 0;
					   }else if(rotX > 135 && rotX < 180 && rotY < 45 && rotY > -45) {
						   BX = 2;
						   	BZ = 2;
					   }else if(rotX < -135 && rotX > -180 && rotY < 45 && rotY > -45) {
						   BX = -2;
						   	BZ = 2;
					   }else if(rotX > 180 && rotX < 225 && rotY < 45 && rotY > -45) {
						   BX = 0;
						   	BZ = 4;
					   }else if(rotX < -180 && rotX > -225 && rotY < 45 && rotY > -45) {
						   BX = 0;
						   	BZ = 4;
					   }else if(rotX > 225 && rotX < 270 && rotY < 45 && rotY > -45) {
						   BX = -2;
						   	BZ = 2;
					   }else if(rotX < -225 && rotX > -270 && rotY < 45 && rotY > -45) {
						   BX = 2;
						   	BZ = 2;
					   }else if(rotX > 270 && rotX < 315 && rotY < 45 && rotY > -45) {
						   BX = -4;
						   	BZ = 0;
					   }else if(rotX < -270 && rotX > -315 && rotY < 45 && rotY > -45) {
						   BX = 4;
						   	BZ = 0;
					   }else if(rotX > 315 && rotX < 360 && rotY < 45 && rotY > -45) {
						   BX = -2;
						   	BZ = -4;
					   }else if(rotX < -315 && rotX > -360 && rotY < 45 && rotY > -45) {
						   BX = 2;
						   	BZ = -2;
					   }
					   float YH = 76;
					   Color.white.bind();

							dirtTexture.bind();
							// Front Face
							glBegin(GL_QUADS);
							glTexCoord2f(0.0f, 0.0f); glVertex3f(-1.0f + -posX+BX+ 12.5f, -1.0f +  1+YH,  1.0f + -posZ+BZ+ 12.5f);	// Bottom Left Of The Texture and Quad
							glTexCoord2f(1.0f, 0.0f); glVertex3f( 1.0f + -posX+BX+ 12.5f , -1.0f +  1+YH,  1.0f + -posZ+BZ+ 12.5f);	// Bottom Right Of The Texture and Quad
							glTexCoord2f(1.0f, 1.0f); glVertex3f( 1.0f + -posX+BX+ 12.5f,  1.0f +  1+YH,  1.0f + -posZ+BZ+ 12.5f);	// Top Right Of The Texture and Quad
							glTexCoord2f(0.0f, 1.0f); glVertex3f(-1.0f + -posX+BX+ 12.5f ,  1.0f +  1+YH,  1.0f + -posZ+BZ+ 12.5f);	// Top Left Of The Texture and Quad
							// Back Face
							glEnd();
							
							Color.white.bind();

							dirtTexture.bind();
							glBegin(GL_QUADS);
							glTexCoord2f(1.0f, 0.0f); glVertex3f(-1.0f+ -posX+BX+ 12.5f, -1.0f+  1+YH, -1.0f + -posZ+BZ+ 12.5f);	// Bottom Right Of The Texture and Quad
							glTexCoord2f(1.0f, 1.0f); glVertex3f(-1.0f+ -posX+BX+ 12.5f,  1.0f+  1+YH, -1.0f+ -posZ +BZ+ 12.5f);	// Top Right Of The Texture and Quad
							glTexCoord2f(0.0f, 1.0f); glVertex3f( 1.0f+ -posX+BX+ 12.5f ,  1.0f+  1+YH, -1.0f+ -posZ+BZ+ 12.5f);	// Top Left Of The Texture and Quad
							glTexCoord2f(0.0f, 0.0f); glVertex3f( 1.0f+ -posX+BX+ 12.5f, -1.0f+  1+YH, -1.0f+ -posZ+BZ+ 12.5f);	// Bottom Left Of The Texture and Quad
							// Top Face
							glEnd();
							Color.white.bind();

							grassTexture.bind();
							glBegin(GL_QUADS);
							glTexCoord2f(0.0f, 1.0f); glVertex3f(-1.0f+ -posX+BX+ 12.5f,  1.0f +  1+YH, -1.0f + -posZ+BZ+ 12.5f);	// Top Left Of The Texture and Quad
							glTexCoord2f(0.0f, 0.0f); glVertex3f(-1.0f+ -posX+BX+ 12.5f,1.0f +  1+YH,  1.0f+ -posZ+BZ+ 12.5f);	// Bottom Left Of The Texture and Quad
							glTexCoord2f(1.0f, 0.0f); glVertex3f( 1.0f+ -posX+BX+ 12.5f,  1.0f +  1+YH,  1.0f + -posZ+BZ+ 12.5f);	// Bottom Right Of The Texture and Quad
							glTexCoord2f(1.0f, 1.0f); glVertex3f( 1.0f+  -posX+BX+ 12.5f ,  1.0f +  1+YH, -1.0f+ -posZ+BZ+ 12.5f);	// Top Right Of The Texture and Quad
							// Bottom Face
							glEnd();
							Color.white.bind();

							dirtTexture.bind();
							glBegin(GL_QUADS);
							glTexCoord2f(1.0f, 1.0f); glVertex3f(-1.0f+ -posX+BX+ 12.5f, -1.0f+  +  1+YH, -1.0f + -posZ+BZ+ 12.5f);	// Top Right Of The Texture and Quad
							glTexCoord2f(0.0f, 1.0f); glVertex3f( 1.0f+ -posX+BX+ 12.5f, -1.0f +  1+YH, -1.0f + -posZ+BZ+ 12.5f);	// Top Left Of The Texture and Quad
							glTexCoord2f(0.0f, 0.0f); glVertex3f( 1.0f+ -posX+BX+ 12.5f, -1.0f +  1+YH,  1.0f + -posZ+BZ+ 12.5f);	// Bottom Left Of The Texture and Quad
							glTexCoord2f(1.0f, 0.0f); glVertex3f(-1.0f+ -posX+BX+ 12.5f, -1.0f +  1+YH,  1.0f + -posZ+BZ+ 12.5f);	// Bottom Right Of The Texture and Quad
							// Right face
							glEnd();
							Color.white.bind();

							dirtTexture.bind();
							glBegin(GL_QUADS);
							glTexCoord2f(1.0f, 0.0f); glVertex3f( 1.0f+ -posX+BX+ 12.5f, -1.0f +  1+YH, -1.0f + -posZ+BZ+ 12.5f);	// Bottom Right Of The Texture and Quad
							glTexCoord2f(1.0f, 1.0f); glVertex3f( 1.0f+ -posX+BX+ 12.5f,  1.0f+  1+YH , -1.0f + -posZ+BZ+ 12.5f);	// Top Right Of The Texture and Quad
							glTexCoord2f(0.0f, 1.0f); glVertex3f( 1.0f+ -posX+BX+ 12.5f,  1.0f +  1+YH,  1.0f + -posZ+BZ+ 12.5f);	// Top Left Of The Texture and Quad
							glTexCoord2f(0.0f, 0.0f); glVertex3f( 1.0f+ -posX+BX+ 12.5f , -1.0f +  1+YH,  1.0f + -posZ+BZ+ 12.5f);	// Bottom Left Of The Texture and Quad
							// Left Face
							glEnd();
							Color.white.bind();

							dirtTexture.bind();
							glBegin(GL_QUADS);
							glTexCoord2f(0.0f, 0.0f); glVertex3f(-1.0f+ -posX+BX+ 12.5f, -1.0f +  1+YH, -1.0f + -posZ+BZ+ 12.5f);	// Bottom Left Of The Texture and Quad
							glTexCoord2f(1.0f, 0.0f); glVertex3f(-1.0f+ -posX+BX+ 12.5f    , -1.0f +  1+YH,  1.0f + -posZ+BZ+ 12.5f);	// Bottom Right Of The Texture and Quad
							glTexCoord2f(1.0f, 1.0f); glVertex3f(-1.0f+ -posX+BX+ 12.5f,  1.0f +  1+YH,  1.0f + -posZ+BZ+ 12.5f);	// Top Right Of The Texture and Quad
							glTexCoord2f(0.0f, 1.0f); glVertex3f(-1.0f+ -posX+BX+ 12.5f ,  1.0f +  1+YH, -1.0f + -posZ+BZ+ 12.5f);	// Top Left Of The Texture and Quad
							 glEnd();
					   
					   
                    	// set the color to white
                    
					chunk.DrawChunk(vdx, vdxn, vdy, vdyn, vdz, vdzn);
					
                    }
                 
                  
                    glLoadMatrix(m.get(fb));
                	glRotatef(rotX, 0.0f,1f,0.0f);
    				glRotatef((float)(rotY *  Math.cos( yawRadian) * Math.cos(90)) ,1.0f,0.0f,0.0f);
    				glRotatef((float)(rotY *  Math.sin( yawRadian) * Math.cos(90) ) ,0.0f,0.0f,1.0f);
                	glTranslatef ( posX - 3,  posY - 2, posZ - 2);
    				System.out.println(player.getX());
    				System.out.println(player.getY());
    				System.out.println(player.getZ());
					glRotatef(0,0.0f,1.0f,0.0f);
					glRotatef(0,1.0f,0.0f,0.0f);
					glRotatef(0,0.0f,0.0f,1.0f);
                   player.drawModel();
                 
					glDisable(GL_TEXTURE_2D);
					
					
			
				
					
	
									// draw the box
	
	angle += 0f;
	
	
	
	
}

public static void UpdateActiveChunks() {
	int i = 0;
	/*for(Chunk chunk : chunks) {
	if((chunk.getX()*25) - posX < 15  && (chunk.getX()*50) - posX >= -15 && (chunk.getY()*50) + posY <=15 && (chunk.getY()*50) + posY >= -15  && (chunk.getX()*25) + posZ <= 15 && (chunk.getX()*25) + posZ >= -15) {
		System.out.println(posX);
	if(i > 1) {
		
		break;
	}
		ativeChunks[i] = chunk;
	
			
		
	i++;
	
	}*/
	
	System.out.println();
	}
public static void placeBlocks(Block block) {
	
}
public static Block binarySearch(Block arr[], int x) 
{ 
    int l = 0, r = arr.length - 1; 
    while (l <= r) { 
        int m = l + (r - l) / 2; 

        // Check if x is present at mid 
        if (arr[m].id == x) 
            return arr[m]; 

        // If x greater, ignore left half 
        if (arr[m].id < x) 
            l = m + 1; 

        // If x is smaller, ignore right half 
        else
            r = m - 1; 
    } 

    // if we reach here, then element was 
    // not present 
    return null; 
} 


public static float getAngle() {
	return angle;
}
public static void setAngle(float angle) {
	DisplayUtills.angle = angle;
}
public static float getPosX() {
	return posX;
}
public static void setPosX(float posX) {
	DisplayUtills.posX = posX;
}
public static float getPosY() {
	return posY;
}
public static void setPosY(float posY) {
	DisplayUtills.posY = posY;
}
public static float getPosZ() {
	return posZ;
}
public static void setPosZ(float posZ) {
	DisplayUtills.posZ = posZ;
}
public static float getRotX() {
	return rotX;
}
public static void setRotX(float rotX) {
	DisplayUtills.rotX = rotX;
}
public static float getRotY() {
	return rotY;
}
public static void setRotY(float rotY) {
	DisplayUtills.rotY = rotY;
}
public static float getRotZ() {
	return rotZ;
}
public static float getRealRotX() {
	return realRotX;
}
public static void setRealRotX(float realRotX) {
	DisplayUtills.realRotX = realRotX;
}


/**
 * Set the display mode to be used 
 * 
 * @param width The width of the display required
 * @param height The height of the display required
 * @param fullscreen True if we want fullscreen mode
 */

}

