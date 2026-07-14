package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2Polygon;
import com.github.xpenatan.box2d.B2Rot;
import com.github.xpenatan.box2d.B2Shape;
import com.github.xpenatan.box2d.B2ShapeDef;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import java.util.ArrayList;
import java.util.List;

/** Java port of Box2D 3.1.1's Continuous / Ghost Bumps sample. */
public final class GhostBumpsSample extends AbstractBox2DSample {
    private B2Body ground;
    private B2Body body;
    private B2Shape movingShape;
    private int shapeType;
    private float round;
    private float friction = .2f;
    private float bevel;
    private boolean useChain = true;

    public GhostBumpsSample() { createScene(); launch(); }

    private void createScene() {
        if(ground != null) destroyBody(ground);
        ground = createStaticBody(0, 0, 0);
        float m = 1.0f / (float)Math.sqrt(2), mm = 2 * ((float)Math.sqrt(2) - 1), hx = 4, hy = .25f;
        if(useChain) {
            float[] p = new float[40]; int n = 0; float x = -3 * hx, y = hy;
            p[n++] = x; p[n++] = y;
            float[][] d = {
                    {-2*hx*m,2*hx*m},{-2*hx*m,2*hx*m},{-2*hx*m,2*hx*m},{-2*hy*m,-2*hy*m},
                    {2*hx*m,-2*hx*m},{2*hx*m,-2*hx*m},{2*hx*m+2*hy*(1-m),-2*hx*m-2*hy*(1-m)},
                    {2*hx+hy*mm,0},{2*hx,0},{2*hx+hy*mm,0},
                    {2*hx*m+2*hy*(1-m),2*hx*m+2*hy*(1-m)},{2*hx*m,2*hx*m},{2*hx*m,2*hx*m},
                    {-2*hy*m,2*hy*m},{-2*hx*m,-2*hx*m},{-2*hx*m,-2*hx*m},{-2*hx*m,-2*hx*m},
                    {-2*hx,0},{-2*hx,0}
            };
            for(float[] delta : d) { x += delta[0]; y += delta[1]; p[n++] = x; p[n++] = y; }
            addChain(ground, p, true, friction);
        }
        else {
            float x = -3*hx - m*hx - m*hy, y = hy + m*hx - m*hy;
            for(int i=0;i<3;i++){ addGroundTile(x,y,-.25f*PI,hx,hy); x-=2*m*hx; y+=2*m*hx; }
            x=-2*hx; y=0; for(int i=0;i<3;i++){ addGroundTile(x,y,0,hx,hy); x+=2*hx; }
            x=3*hx + m*hx + m*hy; y=hy+m*hx-m*hy;
            for(int i=0;i<3;i++){ addGroundTile(x,y,.25f*PI,hx,hy); x+=2*m*hx; y+=2*m*hx; }
        }
    }

    private void addGroundTile(float x, float y, float angle, float hx, float hy) {
        B2Vec2 center = vector(x,y); B2Rot rot = new B2Rot(angle);
        B2Polygon poly = bevel > 0 ? B2Polygon.CreateOffsetRoundedBox(hx,hy,center,rot,Math.min(bevel,.2f))
                : B2Polygon.CreateOffsetBox(hx,hy,center,rot);
        B2ShapeDef def = shapeDef(0,friction,0,0); createPolygonShape(ground,def,poly);
        release(def,poly,rot,center);
    }

    private void launch() {
        if(body != null) destroyBody(body);
        body = createDynamicBody(-28,18,0);
        if(shapeType == 0) movingShape = addCircleShape(body,0,0,.5f,1,friction,0,0);
        else if(shapeType == 1) movingShape = addCapsuleShape(body,-.5f,0,.5f,0,.25f,1,friction,0,0);
        else movingShape = addRoundedBoxShape(body,.5f-round,1-2*round,round,1,friction,0,0);
    }

    @Override public List<Box2DSampleControl> controls() {
        ArrayList<Box2DSampleControl> c = new ArrayList<Box2DSampleControl>();
        c.add(Box2DSampleControl.checkbox("Chain",()->useChain?1:0,v->{useChain=v!=0;createScene();}));
        if(!useChain) c.add(Box2DSampleControl.slider("Bevel",0,1,.01f,()->bevel,v->{bevel=v;createScene();}));
        c.add(Box2DSampleControl.combo("Shape",new String[]{"Circle","Capsule","Box"},()->shapeType,v->shapeType=(int)v));
        if(shapeType==2) c.add(Box2DSampleControl.slider("Round",0,.4f,.1f,()->round,v->round=v));
        c.add(Box2DSampleControl.slider("Friction",0,1,.1f,()->friction,v->{friction=v;if(movingShape!=null)movingShape.SetFriction(v);createScene();}));
        c.add(Box2DSampleControl.button("Launch",this::launch)); return c;
    }
}
