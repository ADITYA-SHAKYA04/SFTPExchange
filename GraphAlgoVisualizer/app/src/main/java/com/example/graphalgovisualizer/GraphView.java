package com.example.graphalgovisualizer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.util.*;

public class GraphView extends View {
    public static class Node {
        public float x, y;
        public int id;
        public Node(int id, float x, float y) { this.id = id; this.x = x; this.y = y; }
    }
    public static class Edge {
        public int from, to, weight;
        public Edge(int from, int to, int weight) { this.from = from; this.to = to; this.weight = weight; }
    }
    private List<Node> nodes = new ArrayList<>();
    private List<Edge> edges = new ArrayList<>();
    private Set<Integer> highlightNodes = new HashSet<>();
    private Set<String> highlightEdges = new HashSet<>();
    private Paint nodePaint, edgePaint, textPaint, highlightPaint;
    private int nodeRadius = 40;
    public GraphView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        nodePaint = new Paint(); nodePaint.setColor(Color.BLUE); nodePaint.setStyle(Paint.Style.FILL);
        edgePaint = new Paint(); edgePaint.setColor(Color.GRAY); edgePaint.setStrokeWidth(8);
        textPaint = new Paint(); textPaint.setColor(Color.WHITE); textPaint.setTextSize(36);
        highlightPaint = new Paint(); highlightPaint.setColor(Color.RED); highlightPaint.setStyle(Paint.Style.FILL);
    }
    public void setGraph(List<Node> nodes, List<Edge> edges) {
        this.nodes = nodes; this.edges = edges; invalidate();
    }
    public void setHighlight(Set<Integer> nodes, Set<String> edges) {
        this.highlightNodes = nodes; this.highlightEdges = edges; invalidate();
    }
    public List<Node> getNodes() { return nodes; }
    public List<Edge> getEdges() { return edges; }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Edge e : edges) {
            Node n1 = nodes.get(e.from), n2 = nodes.get(e.to);
            Paint p = highlightEdges.contains(e.from+"-"+e.to) ? highlightPaint : edgePaint;
            canvas.drawLine(n1.x, n1.y, n2.x, n2.y, p);
            canvas.drawText(String.valueOf(e.weight), (n1.x+n2.x)/2, (n1.y+n2.y)/2, textPaint);
        }
        for (Node n : nodes) {
            Paint p = highlightNodes.contains(n.id) ? highlightPaint : nodePaint;
            canvas.drawCircle(n.x, n.y, nodeRadius, p);
            canvas.drawText(String.valueOf(n.id), n.x-20, n.y+10, textPaint);
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // For adding nodes by touch
        return super.onTouchEvent(event);
    }
}
