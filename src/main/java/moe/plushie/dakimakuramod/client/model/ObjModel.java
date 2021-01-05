package moe.plushie.dakimakuramod.client.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moe.plushie.dakimakuramod.DakimakuraMod;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class ObjModel {

    private Vector3d[] v;
    private Vector2f[] vt;
    private Vector3f[] vn;
    private Face[] faces;
    private int modelList = -1;
    
    private ObjModel(Vector3d[] v, Vector2f[] vt, Vector3f[] vn, Face[] faces) {
        this.v = v;
        this.vt = vt;
        this.vn = vn;
        this.faces = faces;
    }
    
    public void render() {
        if (modelList == -1) { 
            modelList = GLAllocation.generateDisplayLists(1);
            GL11.glNewList(modelList, GL11.GL_COMPILE);
            renderModel();
            GL11.glEndList();
        }
        GL11.glCallList(modelList);
    }
    
    private void renderModel() {
        Tessellator tess = Tessellator.instance;
        tess.startDrawing(GL11.GL_TRIANGLES);
        try {
            for (int i = 0; i < faces.length; i++) {
                Face face = faces[i];
                
                Vector3d v1 = v[face.v1 - 1];
                Vector3d v2 = v[face.v2 - 1];
                Vector3d v3 = v[face.v3 - 1];
                
                Vector2f vt1 = vt[face.vt1 - 1];
                Vector2f vt2 = vt[face.vt2 - 1];
                Vector2f vt3 = vt[face.vt3 - 1];
                
                Vector3f vn1 = vn[face.vn1 - 1];
                Vector3f vn2 = vn[face.vn2 - 1];
                Vector3f vn3 = vn[face.vn3 - 1];
                
                tess.setTextureUV(vt1.x, -vt1.y);
                tess.setNormal(-vn1.x, -vn1.y, vn1.z);
                tess.addVertex(-v1.x, -v1.y, v1.z);

                tess.setTextureUV(vt2.x, -vt2.y);
                tess.setNormal(-vn2.x, -vn2.y, vn2.z);
                tess.addVertex(-v2.x, -v2.y, v2.z);
                
                tess.setTextureUV(vt3.x, -vt3.y);
                tess.setNormal(-vn3.x, -vn3.y, vn3.z);
                tess.addVertex(-v3.x, -v3.y, v3.z);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        tess.draw();
    }
    
    public static ObjModel loadModel(ResourceLocation resourceLocation) {
        byte[] modelData = loadResource(resourceLocation);
        String modelString = new String(modelData);
        String[] modelLines = modelString.split("\\r?\\n");
        
        ArrayList<Vector3d> vList = new ArrayList<Vector3d>();
        ArrayList<Vector2f> vtList = new ArrayList<Vector2f>();
        ArrayList<Vector3f> vnList = new ArrayList<Vector3f>();
        ArrayList<Face> faceList = new ArrayList<Face>();
        
        for (int i = 0; i < modelLines.length; i++) {
            String line = modelLines[i];
            String[] lineSpit = line.split(" ");
            if (lineSpit[0].equals("v")) {
                vList.add(new Vector3d(Double.parseDouble(lineSpit[1]), Double.parseDouble(lineSpit[2]), Double.parseDouble(lineSpit[3])));
            }
            if (lineSpit[0].equals("vt")) {
                vtList.add(new Vector2f(Float.parseFloat(lineSpit[1]), Float.parseFloat(lineSpit[2])));
            }
            if (lineSpit[0].equals("vn")) {
                vnList.add(new Vector3f(Float.parseFloat(lineSpit[1]), Float.parseFloat(lineSpit[2]), Float.parseFloat(lineSpit[3])));
            }
            if (lineSpit[0].equals("f")) {
                faceList.add(new Face(lineSpit[1], lineSpit[2], lineSpit[3]));
            }
        }
        
        Vector3d[] vArray = vList.toArray(new Vector3d[vList.size()]);
        Vector2f[] vtArray = vtList.toArray(new Vector2f[vtList.size()]);
        Vector3f[] vnArray = vnList.toArray(new Vector3f[vnList.size()]);
        Face[] faces = faceList.toArray(new Face[faceList.size()]);
        
        return new ObjModel(vArray, vtArray, vnArray, faces);
    }
    
    private static byte[] loadResource(ResourceLocation resourceLocation) {
        InputStream input = null;
        ByteArrayOutputStream output = null;
        try {
            input = ObjModel.class.getClassLoader().getResourceAsStream("assets/" + resourceLocation.getResourceDomain() + "/" + resourceLocation.getResourcePath());
            if (input != null) {
                output = new ByteArrayOutputStream();
                IOUtils.copy(input, output);
                output.flush();
                byte[] data = output.toByteArray();
                return data;
            } else {
                DakimakuraMod.getLogger().error(String.format("Error extracting file %s.", resourceLocation.toString()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(output);
        }
        return null;
    }
    
    private static class Face {
        // Vertex
        public int v1;
        public int v2;
        public int v3;
        // Texture
        public int vt1;
        public int vt2;
        public int vt3;
        // Normal
        public int vn1;
        public int vn2;
        public int vn3;
        
        //f v1/vt1/vn1 v2/vt2/vn2 v3/vt3/vn3
        
        public Face(String v1, String v2, String v3) {
            String[] s1 = v1.split("/");
            String[] s2 = v2.split("/");
            String[] s3 = v3.split("/");
            
            this.v1 = Integer.parseInt(s1[0]);
            this.vt1 = Integer.parseInt(s1[1]);
            this.vn1 = Integer.parseInt(s1[2]);
            
            this.v2 = Integer.parseInt(s2[0]);
            this.vt2 = Integer.parseInt(s2[1]);
            this.vn2 = Integer.parseInt(s2[2]);
            
            this.v3 = Integer.parseInt(s3[0]);
            this.vt3 = Integer.parseInt(s3[1]);
            this.vn3 = Integer.parseInt(s3[2]);
        }
    }
}
