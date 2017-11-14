# nanothrottler
Simple software throttlers for Java based projects

BurstThrottler - burst all actions ASAP when the time frame start, and then delays to conform to required actions rate.

SmallThrottler - the same as Burst one, but it has less memory requirements, suitable for controlling thousends of actions.

GammaThrottler - tries to spread actions smoothly over time frame; the algorithm is controlled by 'gamma' parameter,
	similar to gamma in image processing (https://en.wikipedia.org/wiki/Gamma_correction).
