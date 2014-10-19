package hu.esgott.caronboard.gl.object;

import javax.media.opengl.GL2;

public interface DrawableObject {

    void update(final long time);

    void draw(final GL2 gl);

}
