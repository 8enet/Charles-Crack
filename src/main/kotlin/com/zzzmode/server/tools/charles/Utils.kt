package com.zzzmode.server.tools.charles

import java.io.*
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.attribute.BasicFileAttributes
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

/**
 * Created by zl on 2017/7/7.
 */


@Throws(IOException::class)
fun replaceZipEntry(zipFile: File, srcFileName: String?, newFile: File?=null,newByteCode:ByteArray?=null) : File {
    val directory = File("tmp")
    if(!directory.exists()) {
        directory.mkdir()
    }


    val tempFile = createTempFile( zipFile.name, null, directory)
    tempFile.delete()


    var fis: FileInputStream? = null
    var fos: FileOutputStream? = null
    var zin: ZipInputStream? = null
    var zout: JarOutputStream? = null

    try {

        fis = FileInputStream(zipFile)
        fos = FileOutputStream(tempFile)
        zin = ZipInputStream(fis)
        zout = JarOutputStream(BufferedOutputStream(fos))

        var entry: ZipEntry? = zin.nextEntry

        val buff = ByteArray(1024*128)


        while (entry != null) {
            val name = entry.name

            val zipEntry = ZipEntry(name)
            zipEntry.method = entry.method
            zipEntry.time = entry.time
            zipEntry.comment = entry.comment
            zipEntry.extra = entry.extra

            if (entry.method == ZipEntry.STORED) {
                zipEntry.size = entry.size
                zipEntry.crc = entry.crc
            }
            zout.putNextEntry(zipEntry)

            if (name == srcFileName) {
                println(name)
                if(newFile != null){
                    val zoutF = zout
                    FileInputStream(newFile).use {
                        copyStream(it, zoutF, buff)
                    }
                }else if(newByteCode != null){
                    zout.write(newByteCode)
                }
            } else {
                copyStream(zin, zout, buff)
            }
            zout.closeEntry()
            entry = zin.nextEntry
        }

        zout.finish()
    } catch (e: IOException) {
        e.printStackTrace()
        throw e
    } finally {

        closeQuietly(zin, zout, fis, fos)
    }
    return tempFile
}

fun closeQuietly(vararg closeable: AutoCloseable?){
    closeable.forEach {
        it?.close()
    }
}

fun copyStream(inputStream: InputStream, outputStream: OutputStream, buff: ByteArray){

    var len = inputStream.read(buff)
    while (len != -1) {
        outputStream.write(buff, 0, len)
        len = inputStream.read(buff)
    }
}

fun File.createTime() : Long {
    return Files.readAttributes(toPath(), BasicFileAttributes::class.java, LinkOption.NOFOLLOW_LINKS)?.creationTime()?.toMillis()?:0
}

fun String.normalizePath():String{
    return trim().filterNot { it == '/' || it == '\\' }
}