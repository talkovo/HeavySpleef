/*
 * This file is part of HeavySpleef.
 * Copyright (c) 2014-2015 matzefratze123
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.matzefratze123.heavyspleef.core.uuid;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import de.matzefratze123.guavacachecompat.CacheCompat;
import de.matzefratze123.guavacachecompat.CacheFactory;
import de.matzefratze123.guavacachecompat.CacheLoaderCompat;

public class UUIDManager {
	
	private static final String BASE_URL = "https://api.mojang.com/profiles/minecraft";
	
	private final JSONParser parser = new JSONParser();
	private final CacheFactory factory = new CacheFactory();
	private final boolean onlineMode;
	
	private CacheCompat<String, GameProfile> profileCache;
	
	public UUIDManager() {
		// Use fast Entity#getUniqueId() when onlineMode = true
		// so we don't have to query the mojang servers
		this.onlineMode = Bukkit.getOnlineMode();
		
		this.profileCache = factory.newCacheBuilder()
				.expireAfterAccess(30, TimeUnit.MINUTES)
				.build(new CacheLoaderCompat<String, GameProfile>() {

					@SuppressWarnings("deprecation")
					@Override
					public GameProfile load(String key) throws Exception {
						GameProfile profile;
						
						try {
							profile = fetchGameProfile(key);
						} catch (PlayerNotRegisteredException e) {
							OfflinePlayer player = Bukkit.getOfflinePlayer(e.getName());
							
							profile = new GameProfile(player.getUniqueId(), player.getName());
						}
						
						return profile;
					}
				});
	}
	
	@SuppressWarnings("deprecation")
	public GameProfile getProfile(String name) throws ExecutionException {
		return getProfile(Bukkit.getOfflinePlayer(name));
	}
	
	public GameProfile getProfile(OfflinePlayer player) throws ExecutionException {
		GameProfile profile = null;
		
		if (onlineMode) {
			// We're in online mode so we can use the bukkit api
			// to construct our GameProfile
			profile = new GameProfile(player.getUniqueId(), player.getName());
		} else {
			profile = profileCache.get(player.getName());
		}
		
		return profile;
	}
	
	private GameProfile fetchGameProfile(String name) throws Exception {
		URL url = new URL(BASE_URL);
		
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setUseCaches(false);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");
		
		String body = JSONArray.toJSONString(Arrays.asList(name));
		
		OutputStream out = connection.getOutputStream();
		out.write(body.getBytes());
		out.flush();
		out.close();
		
		InputStream in = connection.getInputStream();
		Reader reader = new InputStreamReader(in);
		
		JSONArray resultArray = (JSONArray) parser.parse(reader);
		if (!resultArray.isEmpty()) {
			throw new PlayerNotRegisteredException(name);
		}
		
		JSONObject playerResultObject = (JSONObject) resultArray.get(0);
		String id = (String) playerResultObject.get("id");
		
		UUID uuid = getUUID(id);
		return new GameProfile(uuid, name);
	}

	private static UUID getUUID(String id) {
		return UUID.fromString(id.substring(0, 8) + "-" + id.substring(8, 12) + "-" + id.substring(12, 16) + "-" + id.substring(16, 20) + "-"
				+ id.substring(20, 32));
	}

}
