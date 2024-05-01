package me.davethecamper.cashshop.utils;

import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class FileUtils {

	public static void saveFileFromResources(Plugin plugin, File file, String customPath) {
		InputStream inputStream = plugin.getClass().getClassLoader().getResourceAsStream(customPath);

		if (inputStream == null)
			throw new IllegalArgumentException("Arquivo de recursos não encontrado: " + customPath);

		if (!file.exists()) {
			if (!file.getParentFile().exists()) file.getParentFile().mkdirs();

			try {
				file.createNewFile();
			} catch (Exception e) {
				throw new RuntimeException("Ocorreu um erro ao criar o arquivo vazio.", e);
			}
		}


		try (InputStreamReader isr = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
		     BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8))) {

			int character;
			while ((character = isr.read()) != -1) {
				writer.write((char) character);
			}

		} catch (Exception e) {
			throw new RuntimeException("Ocorreu um arro ao salvar o arquivo " + customPath, e);
		}
	}

}
