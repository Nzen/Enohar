/* see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import ws.nzen.format.eno.parse.Grammarian;

/**  */
public class Eno
{

	/** Exposes parsing, for now. 
	 * @throws IOException if toFile is trash. */
	public Section deserialize( Path toFile ) throws IOException
	{
		return new Grammarian().analyze( Files.readAllLines( toFile ) );
	}


	public Section deserialize( String entireDoc )
	{
		return new Grammarian().analyze( Arrays.asList(
				entireDoc.split( System.lineSeparator() ) ) );
	}

}


















