package com.mozhimen.floatk.test

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.opengl.Matrix
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mozhimen.kotlin.utilk.android.graphics.UtilKBitmapFactory
import com.mozhimen.openglk.basic.utils.GLES30Util
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class ForthActivity : AppCompatActivity() {
    private lateinit var _myGLSurfaceView: MyGLSurfaceView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _myGLSurfaceView = MyGLSurfaceView(this)
        setContentView(_myGLSurfaceView)
    }

    override fun onDestroy() {
        super.onDestroy()
        _myGLSurfaceView.release()
    }
}

class MyGLSurfaceView(context: Context) : GLSurfaceView(context) {
    private lateinit var myGLRender: TextureRender

    init {
        setEGLContextClientVersion(3)
        myGLRender = TextureRender(context)
        setRenderer(myGLRender)
    }

    fun release() {
        myGLRender.release()
    }
}


class TextureRender(private val _context: Context) : GLSurfaceView.Renderer {

    private lateinit var _triangle: Texture
    private var _bitmap: Bitmap
    private var _bitmapWidth = 0
    private var _bitmapHeight = 0
    private var _surfaceWidth = 0
    private var _surfaceHeight = 0
    private var _startX = 0
    private var _startY = 0

    private var _viewWidth = 0
    private var _viewHeight = 0

    init {
        _bitmap = getImage()
        _bitmapWidth = _bitmap.width
        _bitmapHeight = _bitmap.height
    }

    private fun getImage(): Bitmap {
        val opts = BitmapFactory.Options().apply {
            inScaled = false
        }
        return UtilKBitmapFactory.decodeResource(_context.resources, R.drawable.xmlk_img, opts)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES30.glClearColor(1.0f, 0.0f, 0.0f, 1f)
        _triangle = Texture(_bitmap)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        _surfaceWidth = width
        _surfaceHeight = height

        //calculateViewport()
        calculateViewport()
        GLES30.glViewport(_startX, _startY, _viewWidth, _viewHeight)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        _triangle.draw()
    }

    fun release() {
        _triangle.release()
    }

    fun calculateViewport() {
        val imageRatio = _bitmapWidth / _bitmapHeight.toFloat()
        val surfaceRatio = _surfaceWidth / _surfaceHeight.toFloat()

        if (imageRatio > surfaceRatio) {
            _viewWidth = _surfaceWidth
            _viewHeight = (_surfaceWidth / imageRatio).toInt()
        } else if (imageRatio < surfaceRatio) {
            _viewHeight = _surfaceHeight
            _viewWidth = (_surfaceHeight * imageRatio).toInt()
        } else {
            _viewWidth = _surfaceWidth
            _viewHeight = _surfaceHeight
        }

        _startX = (_surfaceWidth - _viewWidth) / 2
        _startY = (_surfaceHeight - _viewHeight) / 2
    }
}

class Texture(bitmap: Bitmap) {
    //顶点着色器
    private val _strShaderVertex = """
        uniform mat4 uTMatrix;
        attribute vec4 aPosition;
        attribute vec2 aTexCoord;
        varying vec2 vTexCoord; 
        void main() {
            gl_Position = uTMatrix * aPosition;
            vTexCoord = aTexCoord;
        }
    """.trimIndent()

    //片元着色器
    private val _strShaderFragment = """
        precision mediump float;
        uniform sampler2D uSampler;
        varying vec2 vTexCoord;
        void main() {
            gl_FragColor = texture2D(uSampler,vTexCoord);
        }
    """.trimIndent()

    //顶点
    private val _vertexTexture = floatArrayOf(
        //坐标顶点       纹理坐标
        -1f, 1f, 0f, 0f, 0f,//左上角
        -1f, -1f, 0f, 0f, 1f,//左下角
        1f, 1f, 0f, 1f, 0f,//右上角
        1f, -1f, 0f, 1f, 1f//右下角
    )


    //VBO
    private var _vboIds = IntArray(1)
    private var _matrixTranslate = FloatArray(16)

    private var _vertexBuffer: FloatBuffer
    private var _program: Int = 0
    private var _aPosition = 0
    private var _aTexCoord = 0
    private var _mTMatrix = 0
    private var _uSampler = 0
    private var _textureId = 0

    init {
        _textureId = getTextureId(bitmap)

        val allocateBuffer = ByteBuffer.allocateDirect(_vertexTexture.size * 4)
        allocateBuffer.order(ByteOrder.nativeOrder())
        _vertexBuffer = allocateBuffer.asFloatBuffer()
        _vertexBuffer.put(_vertexTexture)
        _vertexBuffer.position(0)

        //创建shader,并为其指定源码
        val shaderVertex = GLES30Util.loadShader(GLES30.GL_VERTEX_SHADER, _strShaderVertex)
        val shaderFragment = GLES30Util.loadShader(GLES30.GL_FRAGMENT_SHADER, _strShaderFragment)

        _program = GLES30.glCreateProgram()
        GLES30.glAttachShader(_program, shaderVertex)
        GLES30.glAttachShader(_program, shaderFragment)

        GLES30.glLinkProgram(_program)

        GLES30.glDeleteShader(shaderVertex)
        GLES30.glDeleteShader(shaderFragment)

        //生成vbo
        GLES30.glGenBuffers(1, _vboIds, 0)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, _vboIds[0])
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, allocateBuffer.capacity(), allocateBuffer, GLES30.GL_STATIC_DRAW)

        Matrix.setIdentityM(_matrixTranslate, 0)

        //将数据传递给shader
        _aPosition = GLES30.glGetAttribLocation(_program, "aPosition")
        GLES30.glEnableVertexAttribArray(_aPosition)

        _aTexCoord = GLES30.glGetAttribLocation(_program, "aTexCoord")
        GLES30.glEnableVertexAttribArray(_aTexCoord)

        _mTMatrix = GLES30.glGetUniformLocation(_program, "uTMatrix")
        _uSampler = GLES30.glGetUniformLocation(_program, "uSampler")

        GLES30.glVertexAttribPointer(_aPosition, 3, GLES30.GL_FLOAT, false, 0, 0)

        //unbind
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0)
    }


    fun draw() {
        //使用program
        GLES30.glUseProgram(_program)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, _vboIds[0])

        GLES30.glVertexAttribPointer(_aPosition, 3, GLES30.GL_FLOAT, false, 5 * Float.SIZE_BYTES, 0)
        GLES30.glVertexAttribPointer(_aTexCoord, 2, GLES30.GL_FLOAT, false, 5 * Float.SIZE_BYTES, 3 * Float.SIZE_BYTES)

        GLES30.glUniformMatrix4fv(_mTMatrix, 1, false, _matrixTranslate, 0)

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, _textureId)
        GLES30.glUniform1i(_uSampler, 0)

        //drawArray, 绘制三角形
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4)

        //解绑VBO
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0)
    }

    fun draw(mvpM: FloatArray) {
        //使用program
        GLES30.glUseProgram(_program)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, _vboIds[0])

        GLES30.glVertexAttribPointer(_aPosition, 3, GLES30.GL_FLOAT, false, 5 * Float.SIZE_BYTES, 0)
        GLES30.glVertexAttribPointer(_aTexCoord, 2, GLES30.GL_FLOAT, false, 5 * Float.SIZE_BYTES, 3 * Float.SIZE_BYTES)

        GLES30.glUniformMatrix4fv(_mTMatrix, 1, false, mvpM, 0)

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, _textureId)
        GLES30.glUniform1i(_uSampler, 0)

        //drawArray, 绘制三角形
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4)

        //解绑VBO
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0)
    }

    fun release() {
        //program
        GLES30.glDeleteProgram(_program)

        //vbo
        GLES30.glDeleteBuffers(1, _vboIds, 0)
    }


    private fun getTextureId(bitmap: Bitmap): Int {
        val textureIds = IntArray(1)
        GLES30.glGenTextures(1, textureIds, 0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureIds[0])

        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)

        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, bitmap, 0)
        bitmap.recycle()
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)
        return textureIds[0]
    }
}