package fr.aumjaud.antoine.services.secrets.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.aumjaud.antoine.services.common.security.WrongRequestException;

public class FileService {
	private static final Logger logger = LoggerFactory.getLogger(FileService.class);

	private Properties properties;

	/**
	 * Set config 
	 * @param properties the config to set
	 */
	public void setConfig(Properties properties) {
		this.properties = properties;
	}

	/**
	 * Search for a string value in a file
	 * @param fileId the file ID to search in
	 * @param searchValue the value to find
	 * @return the part of file which match the regexp
	 */
	public String search(String fileId, String searchValue) {

		String fileName = properties.getProperty("file." + fileId + ".path");
		if (fileName == null)
			throw new WrongRequestException("fieldId has no filename defined", "The filedId paramater has no filename defined in configuration file");

		logger.debug("Load file: {}", fileName);
		String fileContent;
		try {
			fileContent = Files.lines(Paths.get(fileName)).collect(Collectors.joining("  \n"));
		} catch (IOException e) {
			throw new IllegalStateException("Can't read file: " + fileName, e);
		}

		logger.info("Search '{}' in file: {}", searchValue, fileName);
		Pattern pattern = Pattern.compile(properties.getProperty("file.search.regexp").replace("$VALUE", searchValue));
		Matcher matcher = pattern.matcher(fileContent.toString());
		if (!matcher.find())
			return null;

		return matcher.group(0);
	}
}
