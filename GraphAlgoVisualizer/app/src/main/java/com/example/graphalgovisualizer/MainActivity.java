package com.example.graphalgovisualizer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

public class MainActivity extends AppCompatActivity {
    private GraphView graphView;
    private List<GraphView.Node> nodes = new ArrayList<>();
    private List<GraphView.Edge> edges = new ArrayList<>();
    private int nodeId = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        graphView = findViewById(R.id.graphView);
        Button addNodeBtn = findViewById(R.id.addNodeButton);
        Button addEdgeBtn = findViewById(R.id.addEdgeButton);
        Button runBfsBtn = findViewById(R.id.runBfsButton);
        Button runDfsBtn = findViewById(R.id.runDfsButton);
        Button runDijkstraBtn = findViewById(R.id.runDijkstraButton);
        Button resetBtn = findViewById(R.id.resetButton);
        addNodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                float x = 200 + (float)Math.random()*600;
                float y = 300 + (float)Math.random()*800;
                nodes.add(new GraphView.Node(nodeId++, x, y));
                graphView.setGraph(nodes, edges);
            }
        });
        addEdgeBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (nodes.size() >= 2) {
                    int from = (int)(Math.random()*nodes.size());
                    int to = (int)(Math.random()*nodes.size());
                    if (from != to) edges.add(new GraphView.Edge(from, to, 1+(int)(Math.random()*9)));
                    graphView.setGraph(nodes, edges);
                }
            }
        });
        runBfsBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (!nodes.isEmpty()) visualizeBfs(0);
            }
        });
        runDfsBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (!nodes.isEmpty()) visualizeDfs(0);
            }
        });
        runDijkstraBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (!nodes.isEmpty()) visualizeDijkstra(0);
            }
        });
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                nodes.clear(); edges.clear(); nodeId = 0;
                graphView.setGraph(nodes, edges);
                graphView.setHighlight(new HashSet<>(), new HashSet<>());
            }
        });
    }
    private void visualizeBfs(int start) {
        Set<Integer> visited = new HashSet<>();
        Set<String> edgeSet = new HashSet<>();
        Queue<Integer> q = new LinkedList<>();
        q.add(start); visited.add(start);
        while (!q.isEmpty()) {
            int u = q.poll();
            for (GraphView.Edge e : edges) {
                if (e.from == u && !visited.contains(e.to)) {
                    visited.add(e.to); q.add(e.to);
                    edgeSet.add(e.from+"-"+e.to);
                }
            }
        }
        graphView.setHighlight(visited, edgeSet);
    }
    private void visualizeDfs(int start) {
        Set<Integer> visited = new HashSet<>();
        Set<String> edgeSet = new HashSet<>();
        dfs(start, visited, edgeSet);
        graphView.setHighlight(visited, edgeSet);
    }
    private void dfs(int u, Set<Integer> visited, Set<String> edgeSet) {
        visited.add(u);
        for (GraphView.Edge e : edges) {
            if (e.from == u && !visited.contains(e.to)) {
                edgeSet.add(e.from+"-"+e.to);
                dfs(e.to, visited, edgeSet);
            }
        }
    }
    private void visualizeDijkstra(int start) {
        int n = nodes.size();
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE); dist[start]=0;
        Set<Integer> visited = new HashSet<>();
        Set<String> edgeSet = new HashSet<>();
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a->a[1]));
        pq.add(new int[]{start,0});
        while (!pq.isEmpty()) {
            int[] cur = pq.poll(); int u = cur[0];
            if (visited.contains(u)) continue;
            visited.add(u);
            for (GraphView.Edge e : edges) {
                if (e.from == u && dist[e.to] > dist[u]+e.weight) {
                    dist[e.to] = dist[u]+e.weight;
                    pq.add(new int[]{e.to, dist[e.to]});
                    edgeSet.add(e.from+"-"+e.to);
                }
            }
        }
        graphView.setHighlight(visited, edgeSet);
    }
}
