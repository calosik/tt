package moe.plushie.dakimakuramod.common.dakimakura.pack;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import moe.plushie.dakimakuramod.DakimakuraMod;
import moe.plushie.dakimakuramod.common.dakimakura.Daki;
import moe.plushie.dakimakuramod.common.dakimakura.DakiManager;

public abstract class AbstractDakiPack implements IDakiPack {
    
    protected final DakiManager dakiManager;
    private final String resourceName;
    protected final LinkedHashMap<String, Daki> dakiMap;
    
    protected AbstractDakiPack(String resourceName) {
        dakiManager = DakimakuraMod.getProxy().getDakimakuraManager();
        this.resourceName = resourceName;
        dakiMap = new LinkedHashMap<String, Daki>();
    }
    
    @Override
    public String getResourceName() {
        return resourceName;
    }
    
    @Override
    public int getDakiCount() {
        return dakiMap.size();
    }

    @Override
    public Daki getDaki(String dakiDirName) {
        return dakiMap.get(getResourceName() + ":" + dakiDirName);
    }

    @Override
    public void addDaki(Daki daki) {
        dakiMap.put(getResourceName() + ":" + daki.getDakiDirectoryName(), daki);
    }

    @Override
    public ArrayList<Daki> getDakisInPack() {
        ArrayList<Daki> dakiList = new ArrayList<Daki>();
        for (Daki daki : dakiMap.values()) {
            dakiList.add(daki);
        }
        return dakiList;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((resourceName == null) ? 0 : resourceName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractDakiPack other = (AbstractDakiPack) obj;
        if (resourceName == null) {
            if (other.resourceName != null)
                return false;
        } else if (!resourceName.equals(other.resourceName))
            return false;
        return true;
    }
}
