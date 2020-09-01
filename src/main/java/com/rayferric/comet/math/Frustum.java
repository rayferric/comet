package com.rayferric.comet.math;

public class Frustum {
    public Frustum(Matrix4f viewProjMatrix) {
        for(int i = 0; i < 6; i++)
            planes[i] = new Plane();

        // Left
        planes[0].normal = new Vector3f(
                viewProjMatrix.getX().getW() + viewProjMatrix.getX().getX(),
                viewProjMatrix.getY().getW() + viewProjMatrix.getY().getX(),
                viewProjMatrix.getZ().getW() + viewProjMatrix.getZ().getX()
        );
        planes[0].distance = viewProjMatrix.getW().getW() + viewProjMatrix.getW().getX();

        // Right
        planes[1].normal = new Vector3f(
                viewProjMatrix.getX().getW() - viewProjMatrix.getX().getX(),
                viewProjMatrix.getY().getW() - viewProjMatrix.getY().getX(),
                viewProjMatrix.getZ().getW() - viewProjMatrix.getZ().getX()
        );
        planes[1].distance = viewProjMatrix.getW().getW() - viewProjMatrix.getW().getX();

        // Bottom
        planes[2].normal = new Vector3f(
                viewProjMatrix.getX().getW() + viewProjMatrix.getX().getY(),
                viewProjMatrix.getY().getW() + viewProjMatrix.getY().getY(),
                viewProjMatrix.getZ().getW() + viewProjMatrix.getZ().getY()
        );
        planes[2].distance = viewProjMatrix.getW().getW() + viewProjMatrix.getW().getY();

        // Top
        planes[3].normal = new Vector3f(
                viewProjMatrix.getX().getW() - viewProjMatrix.getX().getY(),
                viewProjMatrix.getY().getW() - viewProjMatrix.getY().getY(),
                viewProjMatrix.getZ().getW() - viewProjMatrix.getZ().getY()
        );
        planes[3].distance = viewProjMatrix.getW().getW() - viewProjMatrix.getW().getY();

        // Near
        planes[4].normal = new Vector3f(
                viewProjMatrix.getX().getW() + viewProjMatrix.getX().getZ(),
                viewProjMatrix.getY().getW() + viewProjMatrix.getY().getZ(),
                viewProjMatrix.getZ().getW() + viewProjMatrix.getZ().getZ()
        );
        planes[4].distance = viewProjMatrix.getW().getW() + viewProjMatrix.getW().getZ();

        // Far
        planes[5].normal = new Vector3f(
                viewProjMatrix.getX().getW() - viewProjMatrix.getX().getZ(),
                viewProjMatrix.getY().getW() - viewProjMatrix.getY().getZ(),
                viewProjMatrix.getZ().getW() - viewProjMatrix.getZ().getZ()
        );
        planes[5].distance = viewProjMatrix.getW().getW() - viewProjMatrix.getW().getZ();

        for(int i = 0; i < 6; i++) {
            Plane plane = planes[i];
            float lengthInv = plane.normal.length();
            plane.normal = plane.normal.mul(lengthInv);
            plane.distance = plane.distance * lengthInv;
        }
    }

    public boolean containsPoint(Vector3f point) {
        for(int i = 0; i < 6; i++) {
            if(planes[i].test(point) < 0) return false;
        }
        return true;
    }

    public boolean containsSphere(Vector3f origin, float radius) {
        for(int i = 0; i < 6; i++) {
            if(planes[i].test(origin) < -radius) return false;
        }
        return true;
    }

    public float distanceToPoint(Vector3f point) {
        float dist = 100000;
        for(int i = 0; i < 6; i++) {
            float d = planes[i].test(point);
            if(d < dist)dist = d;
        }
        return dist;
    }

    private static class Plane {
        public Vector3f normal;
        public float distance;

        public float test(Vector3f point) {
            return point.dot(normal) + distance;
        }
    }

    private final Plane[] planes = new Plane[6];
}
