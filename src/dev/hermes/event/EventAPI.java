/**
 * This is an API used for handling events across your java based projects.
 * It's meant to be simple to use without sacrificing performance and extensibility.
 * <p>
 * Currently the API is in beta phase but it's stable and ready to be used.
 * <p>
 * If you have any suggestion for improvements/fixes for shit,
 * feel free to make a pull request on the bitbucket: https://bitbucket.org/DarkMagician6/eventapi/overview.
 * <p>
 * For information on how to use the API take a look at the wiki:
 * https://bitbucket.org/DarkMagician6/eventapi/wiki/Home
 *
 * @Todo Improve/update the wiki.
 */
package dev.hermes.event;

/**
 * Main class for the API.
 * Contains various information about the API.
 *
 * @author DarkMagician6
 * @since July 31, 2013
 */
public final class EventAPI {

    /**
     * The current version of the API.
     */
    public static final String VERSION = String.format("%s-%s", "0.7", "beta");
    /**
     * Array containing the authors of the API.
     */
    public static final String[] AUTHORS = {
            "DarkMagician6"
    };

    /**
     * No need to create an Object of this class as all Methods are static.
     */
    private EventAPI() {
    }

}
