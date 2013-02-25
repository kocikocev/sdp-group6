package balle.strategy;

import org.apache.log4j.Logger;

import balle.brick.Roboto;
import balle.controller.Controller;
import balle.strategy.planner.AbstractPlanner;
import balle.strategy.planner.GoToBallSafeProportional;
import balle.world.Coord;
import balle.world.Snapshot;
import balle.world.objects.Robot;

public class M3LocateAndShoot2 extends AbstractPlanner {

	// TODO does this dribble towards the goal? it seems to just dribble in a
	// straight line once the initial direction has been set.
	// Do any other dribble executors do a better job?

	private static final Logger LOG = Logger.getLogger(M3LocateAndShoot2.class);
	public static boolean interrupt = Roboto.interrupt;
	
	Milestone2Dribble dribble_executor;
	// DribbleStraight at_ball;
	GoToBallSafeProportional goto_executor;
	Coord startingCoordinate = null;
	Coord currentCoordinate = null;
	Boolean finished = false;
	Boolean arrived = true;

	private static final double MIN_DIST_TO_GOAL = 1.0; // in metres

	public M3LocateAndShoot2() {
		dribble_executor = new Milestone2Dribble();
		goto_executor = new GoToBallSafeProportional();
	}

	@Override
	public void onStep(Controller controller, Snapshot snapshot)
			throws ConfusedException {

		if (interrupt) {
			LOG.info("Wall!");
			return;
		}

		Robot ourRobot = snapshot.getBalle();

		if (finished) {
			return;
		}


		if (ourRobot.possessesBall(snapshot.getBall())
				&& !(ourRobot.getPosition() == null)) {

			LOG.info("At the ball, dribbling");
			currentCoordinate = snapshot.getBalle().getPosition();
			LOG.info(currentCoordinate.dist(snapshot.getOpponentsGoal()
					.getPosition()));
			LOG.info(snapshot.getOpponentsGoal().getGoalLine().toString() + " "
					+ snapshot.getOpponentsGoal().getAccurateGoalLine().toString());
			boolean facingGoal = snapshot.getBalle().getFacingLine()
					.intersects(snapshot.getOpponentsGoal().getAccurateGoalLine());

			if (currentCoordinate.dist(snapshot.getOpponentsGoal()
					.getPosition()) <= MIN_DIST_TO_GOAL && facingGoal) {
				controller.kick();
				LOG.info("Kick!");
				dribble_executor.stop(controller);
				controller.stop();
				LOG.info("Stop!");
				finished = true;
				return;
			}
			else {
				dribble_executor.step(controller, snapshot);
				return;
			}
		} else {
			goto_executor.step(controller, snapshot);
			return;
		}

	}

	@FactoryMethod(designator = "M3LocandShoot", parameterNames = {})
	public static final M3LocateAndShoot2 factory() {
		return new M3LocateAndShoot2();
	}

}
