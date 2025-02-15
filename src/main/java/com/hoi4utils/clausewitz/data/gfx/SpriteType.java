package com.hoi4utils.clausewitz.data.gfx;

import com.hoi4utils.clausewitz.HOIIVUtils;

import java.io.File;

/**
 * This is the SpriteType file.
 */
public class SpriteType {
	String name;			// ex: "GFX_focus_SVA_virginia_officers"
	File texturefile;	   // ex: "gfx/interface/goals/focus_SVA_virginia_officers.dds"

	public SpriteType(String name, File texturefile) {
		this.name = name;
		this.texturefile = texturefile;
	}

	/**
	 *
	 * @param name
	 * @param texturefile abstract file name for texture location of gfx
	 */
	public SpriteType(String name, String texturefile) {
		this(name, new File(texturefile));
	}

	/**
	 * @return texturefile for this gfx
	 */
	public File getTexturefile() {
		return texturefile;
	}

	/**
	 * Returns absolute path texturefile for the gfx.
	 * "Absolute" in this context refers to mod directory + relative path of the texturefile as parsed
	 * @return absolute path texturefile for the gfx
	 */
	public File getTexturefileAbsolute() {
		return new File(HOIIVUtils.get("mod.path") + "\\" + texturefile.getPath());
	}

	/**
	 * Returns the absolute filepath of the texturefile for this spriteType/gfx.
	 * "Absolute" in this context refers to mod directory + relative path of the texturefile as parsed.
	 * @return absolute filepath of texturefile for this gfx
	 */
	public String getGFX() {
		return HOIIVUtils.get("mod.path") + "\\" + texturefile.getPath();
	}

	public String getName() {
			return name;
	}
}
