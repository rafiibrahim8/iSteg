package isteg;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class FilterTheFiles extends FileFilter{

	private String description;
	private String[] extensions;
	
	public FilterTheFiles(String description, String[] extensions) {
		this.description = description;
		this.extensions = extensions;
	}
	
	@Override
	public boolean accept(File file) {
		if(file.isDirectory())
			return true;
		String path = file.getAbsolutePath().toString();
		for(String extension:extensions) {
			if(path.endsWith(extension))
				return true;
		}
		return false;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

}
