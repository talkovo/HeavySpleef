/*
 * This file is part of HeavySpleef.
 * Copyright (c) 2014-2016 Matthias Werning
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
package de.xaniox.heavyspleef.core.persistence;

import de.xaniox.heavyspleef.core.game.Game;
import de.xaniox.heavyspleef.core.stats.Statistic;
import org.dom4j.DocumentException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public interface ReadWriteHandler {
	
	public void saveGames(Iterable<Game> iterable) throws IOException;
	
	public void saveGame(Game game) throws IOException;
	
	public Game getGame(String name) throws IOException, DocumentException;
	
	public List<Game> getGames() throws IOException, DocumentException;
	
	public void renameGame(Game game, String from, String to) throws IOException;
	
	public void deleteGame(Game game) throws IOException;
	
	public void saveStatistics(Iterable<Statistic> iterable) throws SQLException;
	
	public void saveStatistic(Statistic statistic) throws SQLException;
	
	public Statistic getStatistic(UUID uuid) throws Exception;
	
	public Statistic getStatistic(String playerName) throws Exception;
	
	public Integer getStatisticRank(String player) throws Exception;

	public Integer getStatisticRank(UUID uuid) throws Exception;

	public Map<String, Statistic> getStatistics(String[] players) throws Exception;
	
	public Map<String, Statistic> getTopStatistics(int offset, int limit) throws SQLException, ExecutionException;

	public void clearCache();

	public void forceCacheSave() throws SQLException;

	public void release();

	public void shutdownGracefully();
	
}