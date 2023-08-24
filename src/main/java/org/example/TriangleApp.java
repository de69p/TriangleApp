package org.example;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class TriangleApp extends PApplet {
    ArrayList<Triangle> triangleObjects = new ArrayList<>();

    public static void main(String[] args) {
        PApplet.main("org.example.TriangleApp");
    }

    // Set window settings
    public void settings() {
        size(1200, 1000);
    }

    // Initialize the program
    public void setup() {
        int rows = 4;
        int cols = 10;
        float side = 100;
        float distanceBetweenTriangles = 0;
        float distanceBetweenRows = 120;
        float totalWidth = cols * (side + distanceBetweenTriangles);
        float offsetX = (width - totalWidth) / 2;
        float offsetY = (height - (rows * (side * sqrt(3) / 2 + distanceBetweenRows))) / 2;

        // Create a grid of triangles with specific positions and rotations
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                float x = col * (side + distanceBetweenTriangles) + offsetX;
                float y = row * (side * sqrt(3) / 2 + distanceBetweenRows) + offsetY;
                boolean rotate = col % 2 == 0;

                if (rotate) {
                    y += 50;
                }

                PVector[] triangle = new PVector[3];

                if (!rotate) {
                    triangle[0] = new PVector(x, y);
                    triangle[1] = new PVector(x + side, y);
                    triangle[2] = new PVector(x + side / 2, y + side * sqrt(3) / 2);
                } else {
                    triangle[0] = new PVector(x + side / 2, y + side * sqrt(3) / 2);
                    triangle[1] = new PVector(x, y);
                    triangle[2] = new PVector(x + side, y);
                }

                if (rotate) {
                    for (PVector vertex : triangle) {
                        vertex.x = 2 * (x + side / 2) - vertex.x;
                        vertex.y = 2 * (y + side * sqrt(3) / 4) - vertex.y;
                    }
                }

                triangleObjects.add(new Triangle(triangle, false));
            }
        }

        // Set initial target scale for all triangles
        for (Triangle t : triangleObjects) {
            t.targetScale = 1.5F;
        }
    }

    // Update and display
    public void draw() {
        background(175);
        for (Triangle t : triangleObjects) {
            if (t.containsPoint(new PVector(mouseX, mouseY))) {
                t.targetScale = 2.25F;
            } else {
                t.targetScale = 1.5F;
            }
            t.update();
            t.display();
        }
    }

    // Handle mouse press
    public void mousePressed() {
        int clickedIndex = -1;

        // Check if any triangle is clicked
        for (int i = 0; i < triangleObjects.size(); i++) {
            Triangle t = triangleObjects.get(i);
            if (t.containsPoint(new PVector(mouseX, mouseY))) {
                clickedIndex = i;
                break;
            }
        }

        if (clickedIndex != -1) {
            Triangle clickedTriangle = triangleObjects.get(clickedIndex);
            clickedTriangle.fillCol = clickedTriangle.randomColor();
            clickedTriangle.isClicked = !clickedTriangle.isClicked;
            clickedTriangle.rotationDirection = (mouseButton == RIGHT) ? 1 : -1; // Set rotation direction
            triangleObjects.remove(clickedIndex);
            triangleObjects.add(clickedTriangle);
        }
    }

    // Class representing a triangle
    class Triangle {
        PVector[] vertices;
        int fillCol;
        float rotationAngle = 0;
        boolean isClicked = false;
        boolean initialRotate;
        float scaleFactor = 1.5f;
        float targetScale = 1;
        float scaleSpeed = 0.05f;
        float rotationDirection = 1;

        Triangle(PVector[] verts, boolean rotate) {
            vertices = verts;
            fillCol = randomColor();
            initialRotate = rotate;
        }

        // Generate a random color
        int randomColor() {
            return color(random(255), random(255), random(255));
        }

        // Find the center of the triangle
        PVector center() {
            return new PVector((vertices[0].x + vertices[1].x + vertices[2].x) / 3,
                    (vertices[0].y + vertices[1].y + vertices[2].y) / 3);
        }

        // Update the scale
        void update() {
            scaleFactor += (targetScale - scaleFactor) * scaleSpeed;
        }

        // Display the triangle
        void display() {
            pushMatrix();
            PVector centerPt = center();
            translate(centerPt.x, centerPt.y);
            scale(scaleFactor);
            if (initialRotate) {
                rotate(PI);
            }
            if (isClicked) {
                rotate(rotationAngle);
                rotationAngle += PI / 60 * rotationDirection;
                if (Math.abs(rotationAngle) >= TWO_PI) {
                    isClicked = false;
                    rotationAngle = 0;
                }
            }
            fill(fillCol);
            stroke(0);
            strokeWeight(5);
            translate(-centerPt.x, -centerPt.y);
            beginShape();
            for (PVector v : vertices) {
                vertex(v.x, v.y);
            }
            endShape(CLOSE);
            popMatrix();
        }

        // Check if the triangle contains a given point
        boolean containsPoint(PVector pt) {
            float detT = (vertices[1].y - vertices[2].y) * (vertices[0].x - vertices[2].x) + (vertices[2].x - vertices[1].x) * (vertices[0].y - vertices[2].y);
            float alpha = ((vertices[1].y - vertices[2].y) * (pt.x - vertices[2].x) + (vertices[2].x - vertices[1].x) * (pt.y - vertices[2].y)) / detT;
            float beta = ((vertices[2].y - vertices[0].y) * (pt.x - vertices[2].x) + (vertices[0].x - vertices[2].x) * (pt.y - vertices[2].y)) / detT;
            float gamma = 1.0f - alpha - beta;

            return alpha > 0 && beta > 0 && gamma > 0;
        }
    }
}
