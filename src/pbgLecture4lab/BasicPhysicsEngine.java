package pbgLecture4lab;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;


public class BasicPhysicsEngine {
	/* Author: Michael Fairbank
	 * Creation Date: 2016-01-28
	 * Significant changes applied:
	 */
	
	// frame dimensions
	public static final int SCREEN_HEIGHT = 680;
	public static final int SCREEN_WIDTH = 640;
	public static final Dimension FRAME_SIZE = new Dimension(
			SCREEN_WIDTH, SCREEN_HEIGHT);
	public static final double WORLD_WIDTH=10;//metres
	public static final double WORLD_HEIGHT=SCREEN_HEIGHT*(WORLD_WIDTH/SCREEN_WIDTH);// meters - keeps world dimensions in same aspect ratio as screen dimensions, so that circles get transformed into circles as opposed to ovals
	public static final double GRAVITY=9.8;

	// sleep time between two drawn frames in milliseconds 
	public static final int DELAY = 20;
	public static final int NUM_EULER_UPDATES_PER_SCREEN_REFRESH=10;
	// estimate for time between two frames in seconds 
	public static final double DELTA_T = DELAY / 1000.0 / NUM_EULER_UPDATES_PER_SCREEN_REFRESH / 4;
	
	
	public static int convertWorldXtoScreenX(double worldX) {
		return (int) (worldX/WORLD_WIDTH*SCREEN_WIDTH);
	}
	public static int convertWorldYtoScreenY(double worldY) {
		// minus sign in here is because screen coordinates are upside down.
		return (int) (SCREEN_HEIGHT-(worldY/WORLD_HEIGHT*SCREEN_HEIGHT));
	}
	public static int convertWorldLengthToScreenLength(double worldLength) {
		return (int) (worldLength/WORLD_WIDTH*SCREEN_WIDTH);
	}
	public static double convertScreenXtoWorldX(int screenX) {
		// to get this to work you need to program the inverse function to convertWorldXtoScreenX
		// this means rearranging the equation z=(worldX/WORLD_WIDTH*SCREEN_WIDTH) to make worldX the subject, 
		// and then returning worldX
		// Ask for help if you need it!
                return screenX * WORLD_WIDTH / SCREEN_WIDTH;
	}
	public static double convertScreenYtoWorldY(int screenY) {
		// to get this to work you need to program the inverse function to convertWorldYtoScreenY
		// this means rearranging the equation z= (SCREEN_HEIGHT-(worldY/WORLD_HEIGHT*SCREEN_HEIGHT)) to make 
		// worldY the subject, and then returning worldY
		// Ask for help if you need it!
                return -1 * ((screenY - SCREEN_HEIGHT) * WORLD_HEIGHT / SCREEN_HEIGHT);
	}
	
	
        // Simple pendulum attached under mouse pointer
	public static double rollingFriction=5;
	public static double springConstant=20000, springDampingConstant=1000;
	public static Double hookesLawTruncation=null;
	public static boolean canGoSlack=false;
        public static boolean connected = false;
        public static boolean leavedBarrier = false;
        public static boolean spaceShipStarted = false;
        public AnchoredBarrier_StraightLine startBarrierBall;
        public AnchoredBarrier_StraightLine winningBarrier;
	
	public List<BasicParticle> particles;
	public List<AnchoredBarrier> barriers;
	public List<ElasticConnector> connectors;
	
	public static enum LayoutMode {CONVEX_ARENA, CONCAVE_ARENA, CONVEX_ARENA_WITH_CURVE, PINBALL_ARENA, RECTANGLE, SNOOKER_TABLE, THRUST_ARENA};
	public BasicPhysicsEngine() {
		particles = new ArrayList<BasicParticle>();
		barriers = new ArrayList<AnchoredBarrier>();
		// empty particles array, so that when a new thread starts it clears current particle state:
		particles = new ArrayList<BasicParticle>();
		connectors=new ArrayList<ElasticConnector>();
		LayoutMode layout=LayoutMode.THRUST_ARENA;
		// pinball:
		double r=.1;
		
		particles.add(new ControllableSpaceShip(1,WORLD_HEIGHT/10 * 8.1,0,0, r, true, 10000));
		particles.add(new BasicParticle(9,WORLD_HEIGHT/48 * 1.4,0,0, r,true, Color.BLUE, 2*4, rollingFriction));
		//connectors.add(new ElasticConnector(particles.get(0), particles.get(1), 1, springConstant, springDampingConstant, false, Color.WHITE, hookesLawTruncation));

		

		
		barriers = new ArrayList<AnchoredBarrier>();
		
		switch (layout) {
			case RECTANGLE: {
				// rectangle walls:
				// anticlockwise listing
				barriers.add(new AnchoredBarrier_StraightLine(0, 0, WORLD_WIDTH, 0, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, 0, WORLD_WIDTH, WORLD_HEIGHT, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT, 0, WORLD_HEIGHT, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(0, WORLD_HEIGHT, 0, 0, Color.WHITE));
				break;
			}
			case CONVEX_ARENA: {
				barriers.add(new AnchoredBarrier_StraightLine(0,WORLD_HEIGHT/3, WORLD_WIDTH/2, 0, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH/2, 0, WORLD_WIDTH, WORLD_HEIGHT/3, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT/3, WORLD_WIDTH, WORLD_HEIGHT, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT, 0, WORLD_HEIGHT, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(0, WORLD_HEIGHT, 0, WORLD_HEIGHT/3, Color.WHITE));
				break;
			}
			case CONCAVE_ARENA: {
				barriers.add(new AnchoredBarrier_StraightLine(0,WORLD_HEIGHT/3, WORLD_WIDTH/2, 0, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH/2, 0, WORLD_WIDTH, WORLD_HEIGHT/3, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT/3, WORLD_WIDTH, WORLD_HEIGHT, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT, 0, WORLD_HEIGHT, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(0, WORLD_HEIGHT, 0, WORLD_HEIGHT/3, Color.WHITE));
				double width=WORLD_HEIGHT/20;
				barriers.add(new AnchoredBarrier_StraightLine(0, WORLD_HEIGHT*2/3, WORLD_WIDTH/2, WORLD_HEIGHT*1/2, Color.WHITE,width/10));
				barriers.add(new AnchoredBarrier_Point(WORLD_WIDTH/2, WORLD_HEIGHT*1/2));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH/2, WORLD_HEIGHT*1/2, WORLD_WIDTH/2, WORLD_HEIGHT*1/2-width, Color.WHITE,width/10));
				barriers.add(new AnchoredBarrier_Point(WORLD_WIDTH/2, WORLD_HEIGHT*1/2-width));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH/2, WORLD_HEIGHT*1/2-width, 0, WORLD_HEIGHT*2/3-width, Color.WHITE,width/10));
				break;
			}
			case CONVEX_ARENA_WITH_CURVE: {
				barriers.add(new AnchoredBarrier_StraightLine(0,WORLD_HEIGHT/3, WORLD_WIDTH/2, 0, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH/2, 0, WORLD_WIDTH, WORLD_HEIGHT/3, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT/3, WORLD_WIDTH, WORLD_HEIGHT, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT, 0, WORLD_HEIGHT, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(0, WORLD_HEIGHT, 0, 0, Color.WHITE));
				barriers.add(new AnchoredBarrier_Curve(WORLD_WIDTH/2, WORLD_HEIGHT-WORLD_WIDTH/2, WORLD_WIDTH/2, 0.0, 180.0,true, Color.WHITE));
				break;
			}
			case PINBALL_ARENA: {
				// simple pinball board
				barriers.add(new AnchoredBarrier_StraightLine(0, 0, WORLD_WIDTH, 0, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, 0, WORLD_WIDTH, WORLD_HEIGHT, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT, 0, WORLD_HEIGHT, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(0, WORLD_HEIGHT, 0, 0, Color.WHITE));
				barriers.add(new AnchoredBarrier_Curve(WORLD_WIDTH/2, WORLD_HEIGHT-WORLD_WIDTH/2, WORLD_WIDTH/2, 0.0, 200.0,true, Color.WHITE));
				barriers.add(new AnchoredBarrier_Curve(WORLD_WIDTH/2, WORLD_HEIGHT*3/4, WORLD_WIDTH/15, -0.0, 360.0,false, Color.WHITE));
				barriers.add(new AnchoredBarrier_Curve(WORLD_WIDTH*1/3, WORLD_HEIGHT*1/2, WORLD_WIDTH/15, -0.0, 360.0,false, Color.WHITE));
				barriers.add(new AnchoredBarrier_Curve(WORLD_WIDTH*2/3, WORLD_HEIGHT*1/2, WORLD_WIDTH/15, -0.0, 360.0,false, Color.WHITE));
				break;
			}
			case SNOOKER_TABLE: {
				double snookerTableHeight=WORLD_HEIGHT;
				double pocketSize=0.4;
				double cushionDepth=0.3;
				double cushionLength = snookerTableHeight/2-pocketSize-cushionDepth;
				double snookerTableWidth=cushionLength+cushionDepth*2+pocketSize*2;
				
				createCushion(barriers, snookerTableWidth-cushionDepth/2, snookerTableHeight*0.25,0, cushionLength, cushionDepth); 
				createCushion(barriers, snookerTableWidth-cushionDepth/2, snookerTableHeight*0.75,0, cushionLength, cushionDepth); 
				createCushion(barriers, snookerTableWidth/2, snookerTableHeight-cushionDepth/2, Math.PI/2, cushionLength, cushionDepth); 
				createCushion(barriers, cushionDepth/2, snookerTableHeight*0.25,Math.PI, cushionLength, cushionDepth); 
				createCushion(barriers, cushionDepth/2, snookerTableHeight*0.75,Math.PI, cushionLength, cushionDepth); 
				createCushion(barriers, snookerTableWidth/2, cushionDepth/2, Math.PI*3/2, cushionLength, cushionDepth); 
				
				
				break;
			}
                        case THRUST_ARENA: {
                                startBarrierBall = new AnchoredBarrier_StraightLine(WORLD_WIDTH / 8, WORLD_HEIGHT / 48, WORLD_WIDTH, WORLD_HEIGHT / 48, Color.WHITE, 0.1);
                                barriers.add(startBarrierBall);
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT / 48, WORLD_WIDTH, WORLD_HEIGHT / 4, Color.WHITE, 0.1));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT / 4, WORLD_WIDTH / 3, WORLD_HEIGHT / 4, Color.WHITE, 0.1));
                                barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH / 3, WORLD_HEIGHT / 4, WORLD_WIDTH / 3, WORLD_HEIGHT / 4, Color.WHITE, 0.1));
                                barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT / 4, WORLD_WIDTH, WORLD_HEIGHT, Color.WHITE, 0.1));
                                winningBarrier = new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT, 0, WORLD_HEIGHT, Color.WHITE, 0.1);
                                barriers.add(winningBarrier);
				barriers.add(new AnchoredBarrier_StraightLine(0, WORLD_HEIGHT, 0, WORLD_HEIGHT / 5 * 4, Color.WHITE, 0.1));
                                barriers.add(new AnchoredBarrier_StraightLine(0, WORLD_HEIGHT / 5 * 4, WORLD_WIDTH / 3 * 2, WORLD_HEIGHT / 5 * 4, Color.WHITE, 0.1));
                                barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH / 3 * 2, WORLD_HEIGHT / 5 * 4, WORLD_WIDTH / 3 * 2, WORLD_HEIGHT / 32 * 15, Color.WHITE, 0.1));
                                barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH / 3 * 2, WORLD_HEIGHT / 32 * 15, WORLD_WIDTH / 8, WORLD_HEIGHT / 32 * 15, Color.WHITE, 0.1));
                                barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH / 8, WORLD_HEIGHT / 32 * 15, WORLD_WIDTH / 8, WORLD_HEIGHT / 48, Color.WHITE, 0.1));
                        }
		}
			
			
	//	
	//	particles.add(new BasicParticle(r,r,-3,12, r));
//		particles.add(new BasicParticle(0,0,4,10, r,true, Color.BLUE, includeInbuiltCollisionDetection, 2));
//		particles.add(new BasicParticle(0,0,4,10, r,false, Color.RED, includeInbuiltCollisionDetection, 2));
	}
	private void createCushion(List<AnchoredBarrier> barriers, double centrex, double centrey, double orientation, double cushionLength, double cushionDepth) {
		// on entry, we require centrex,centrey to be the centre of the rectangle that contains the cushion.
		Color col=Color.WHITE;
		Vector2D p1=new Vector2D(cushionDepth/2, -cushionLength/2-cushionDepth/2);
		Vector2D p2=new Vector2D(-cushionDepth/2, -cushionLength/2);
		Vector2D p3=new Vector2D(-cushionDepth/2, +cushionLength/2);
		Vector2D p4=new Vector2D(cushionDepth/2, cushionLength/2+cushionDepth/2);
		p1.rotate(orientation);
		p2.rotate(orientation);
		p3.rotate(orientation);
		p4.rotate(orientation);
		// we are being careful here to list edges in an anticlockwise manner, so that normals point inwards!
		barriers.add(new AnchoredBarrier_StraightLine(centrex+p1.x, centrey+p1.y, centrex+p2.x, centrey+p2.y, col));
		barriers.add(new AnchoredBarrier_StraightLine(centrex+p2.x, centrey+p2.y, centrex+p3.x, centrey+p3.y, col));
		barriers.add(new AnchoredBarrier_StraightLine(centrex+p3.x, centrey+p3.y, centrex+p4.x, centrey+p4.y, col));
		// oops this will have concave corners so will need to fix that some time! 
	}
	public static void main(String[] args) throws Exception {
		final BasicPhysicsEngine game = new BasicPhysicsEngine();
		final BasicView view = new BasicView(game);
		JEasyFrame frame = new JEasyFrame(view, "Thrust Game");
		frame.addKeyListener(new BasicKeyListener());
		view.addMouseMotionListener(new BasicMouseListener());
		game.startThread(view);
	}
	private void startThread(final BasicView view) throws InterruptedException {
		final BasicPhysicsEngine game=this;
		while (true) {
			for (int i=0;i<NUM_EULER_UPDATES_PER_SCREEN_REFRESH;i++) {
				game.update();
			}
			view.repaint();

			try {
				Thread.sleep(DELAY);
			} catch (InterruptedException e) {
			}
		}
	}
	


	public void update() {
		for (BasicParticle p : particles) {
			p.resetTotalForce();// reset to zero at start of time step, so accumulation of forces can begin.
		}
		for (ElasticConnector ec: connectors) {
			ec.applyTensionForceToBothParticles();
		}
		for (BasicParticle p : particles) {
			p.update(); // tell each particle to move
		}
		for (BasicParticle particle : particles) {
			for (AnchoredBarrier b : barriers) {
				if (b.isCircleCollidingBarrier(particle.getPos(), particle.getRadius())) {
					Vector2D bouncedVel=b.calculateVelocityAfterACollision(particle.getPos(), particle.getVel());
					particle.setVel(bouncedVel);
                                        if (b == winningBarrier && connected) {
                                            System.exit(2);
                                        } else if ((connected && leavedBarrier) || particle == particles.get(0) && spaceShipStarted) {
                                            System.exit(1);
                                        }
				}
			}
		}
		double e=0.9; // coefficient of restitution for all particle pairs
		for (int n=0;n<particles.size();n++) {
			for (int m=0;m<n;m++) {// avoids double check by requiring m<n
				BasicParticle p1 = particles.get(n);
				BasicParticle p2 = particles.get(m);
				if (p1.collidesWith(p2)) {
					BasicParticle.implementElasticCollision(p1, p2, e);
				}
			}
		}
                if (!connected && BasicKeyListener.isSpaceBarPressed() && particles.get(0).getPos().dist(particles.get(1).getPos()) < 2) {
                    connectors.add(new ElasticConnector(particles.get(0), particles.get(1), 1, springConstant, springDampingConstant, false, Color.WHITE, hookesLawTruncation));
                    connected = true;
                    System.out.println(particles.get(0).getPos().dist(particles.get(1).getPos()));
                }
                if (!leavedBarrier) {
                    if (!startBarrierBall.isCircleCollidingBarrier(particles.get(1).getPos(), particles.get(1).getRadius()) && connected) {
                        leavedBarrier = true;
                    }
                }
                if (!spaceShipStarted && BasicKeyListener.isThrustKeyPressed()) {
                    spaceShipStarted = true;
                }
			
	}
	
	
	

}


