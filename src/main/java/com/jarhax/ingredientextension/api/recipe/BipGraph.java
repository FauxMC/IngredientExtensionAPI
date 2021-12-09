package com.jarhax.ingredientextension.api.recipe;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class BipGraph {
    
    private static final int NIL = 0;
    private static final int INF = Integer.MAX_VALUE;
    private final int size;
    
    IntList[] adj;
    
    public BipGraph(int size) {
        
        this.size = size;
        adj = new IntList[size + 1];
        Arrays.fill(adj, new IntArrayList());
    }
    
    public void addEdge(int u, int v) {
        
        adj[u].add(v);
    }
    
    public int[] pairU;
    public int[] pairV;
    public int[] dist;
    
    public int hopcroftKarp() {
        
        pairU = new int[size + 1];
        
        pairV = new int[size + 1];
        
        dist = new int[size + 1];
        
        Arrays.fill(pairU, NIL);
        Arrays.fill(pairV, NIL);
        
        int result = 0;
        
        while(bfs()) {
            
            for(int u = 1; u <= size; u++) {
                if(pairU[u] == NIL && dfs(u)) {
                    result++;
                }
            }
        }
        return result;
    }
    
    public boolean bfs() {
        Queue<Integer> Q = new LinkedList<>();
        
        for(int u = 1; u <= size; u++) {
            
            if(pairU[u] == NIL) {
                
                dist[u] = 0;
                Q.add(u);
            } else {
                dist[u] = INF;
            }
        }
        
        dist[NIL] = INF;
        
        while(!Q.isEmpty()) {
            
            int u = Q.poll();
            
            if(dist[u] < dist[NIL]) {
                
                for(int i : adj[u]) {
                    
                    if(dist[pairV[i]] == INF) {
                        
                        dist[pairV[i]] = dist[u] + 1;
                        Q.add(pairV[i]);
                    }
                }
            }
        }
        
        return (dist[NIL] != INF);
    }
    
    public boolean dfs(int u) {
        
        if(u != NIL) {
            for(int i : adj[u]) {
                
                if(dist[pairV[i]] == dist[u] + 1) {
                    
                    if(dfs(pairV[i])) {
                        pairV[i] = u;
                        pairU[u] = i;
                        return true;
                    }
                }
            }
            
            dist[u] = INF;
            return false;
        }
        return true;
    }
    
    
}