package de.matzefratze123.api.hs.command.transform;

public interface Transformer<T> {

	public T transform(String argument) throws TransformException;

}
