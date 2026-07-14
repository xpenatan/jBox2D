package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.sample.shared.Box2DSampleCamera;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** The Java sample catalog, in the same category/name order as Box2D 3.1.1. */
public final class Box2DSampleRegistry {
    private static final List<Box2DSampleEntry> ENTRIES = createEntries();

    private Box2DSampleRegistry() {
    }

    public static List<Box2DSampleEntry> entries() { return ENTRIES; }

    private static List<Box2DSampleEntry> createEntries() {
        ArrayList<Box2DSampleEntry> entries = new ArrayList<Box2DSampleEntry>(110);
        add(entries, "Benchmark", "Barrel", BenchmarkBarrelSample::new, 8.0f, 53.0f, 58.75f);
        add(entries, "Benchmark", "Tumbler", BenchmarkTumblerSample::new, 1.5f, 10.0f, 15.0f);
        add(entries, "Benchmark", "Many Tumblers", BenchmarkManyTumblersSample::new, 1.0f, -5.5f, 85.0f);
        add(entries, "Benchmark", "Large Pyramid", BenchmarkLargePyramidSample::new, 0.0f, 50.0f, 55.0f);
        add(entries, "Benchmark", "Many Pyramids", BenchmarkManyPyramidsSample::new, 16.0f, 110.0f, 125.0f);
        add(entries, "Benchmark", "CreateDestroy", BenchmarkCreateDestroySample::new, 0.0f, 50.0f, 55.0f);
        add(entries, "Benchmark", "Sleep", BenchmarkSleepSample::new, 0.0f, 50.0f, 55.0f);
        add(entries, "Benchmark", "Joint Grid", BenchmarkJointGridSample::new, 60.0f, -57.0f, 62.5f);
        add(entries, "Benchmark", "Smash", BenchmarkSmashSample::new, 60.0f, 6.0f, 40.0f);
        add(entries, "Benchmark", "Compound", BenchmarkCompoundSample::new, 18.0f, 115.0f, 137.5f);
        add(entries, "Benchmark", "Kinematic", BenchmarkKinematicSample::new, 0.0f, 0.0f, 150.0f);
        add(entries, "Benchmark", "Cast", BenchmarkCastSample::new, 500.0f, 500.0f, 525.0f);
        add(entries, "Benchmark", "Spinner", BenchmarkSpinnerSample::new, 0.0f, 32.0f, 42.0f);
        add(entries, "Benchmark", "Rain", BenchmarkRainSample::new, 0.0f, 110.0f, 125.0f);
        add(entries, "Benchmark", "Shape Distance", BenchmarkShapeDistanceSample::new, 0.0f, 0.0f, 3.0f);
        add(entries, "Benchmark", "Sensor", BenchmarkSensorSample::new, 0.0f, 105.0f, 125.0f);
        add(entries, "Stacking", "Single Box", SingleBoxSample::new, 0.0f, 2.5f, 3.5f);
        add(entries, "Stacking", "Tilted Stack", TiltedStackSample::new, 7.5f, 7.5f, 20.0f);
        add(entries, "Stacking", "Vertical Stack", VerticalStackSample::new, -7.0f, 9.0f, 14.0f);
        add(entries, "Stacking", "Circle Stack", CircleStackSample::new, 0.0f, 5.0f, 6.0f);
        add(entries, "Stacking", "Capsule Stack", CapsuleStackSample::new, 0.0f, 5.0f, 6.0f);
        add(entries, "Stacking", "Cliff", CliffSample::new, 0.0f, 5.0f, 12.5f);
        add(entries, "Stacking", "Arch", ArchSample::new, 0.0f, 8.0f, 8.75f);
        add(entries, "Stacking", "Double Domino", DoubleDominoSample::new, 0.0f, 4.0f, 6.25f);
        add(entries, "Stacking", "Confined", ConfinedSample::new, 0.0f, 10.0f, 12.5f);
        add(entries, "Stacking", "Card House", CardHouseSample::new, 0.75f, 0.9f, 1.25f);
        add(entries, "Bodies", "Body Type", BodyTypeSample::new, 0.8f, 6.4f, 10.0f);
        add(entries, "Bodies", "Weeble", WeebleSample::new, 2.3f, 10.0f, 12.5f);
        add(entries, "Bodies", "Sleep", SleepSample::new, 3.0f, 50.0f, 55.0f);
        add(entries, "Bodies", "Bad", BadBodySample::new, 2.3f, 10.0f, 12.5f);
        add(entries, "Bodies", "Pivot", PivotSample::new, 0.8f, 6.4f, 10.0f);
        add(entries, "Bodies", "Kinematic", KinematicSample::new, 0.0f, 0.0f, 4.0f);
        add(entries, "Character", "Mover", MoverSample::new, 20.0f, 9.0f, 10.0f);
        add(entries, "Collision", "Shape Distance", ShapeDistanceSample::new, 0.0f, 0.0f, 3.0f);
        add(entries, "Collision", "Dynamic Tree", DynamicTreeSample::new, 500.0f, 500.0f, 525.0f);
        add(entries, "Collision", "Ray Cast", RayCastSample::new, 0.0f, 20.0f, 17.5f);
        add(entries, "Collision", "Cast World", CastWorldSample::new, 2.0f, 14.0f, 18.75f);
        add(entries, "Collision", "Overlap World", OverlapWorldSample::new, 0.0f, 10.0f, 17.5f);
        add(entries, "Collision", "Manifold", ManifoldSample::new, 1.8f, 0.0f, 11.25f);
        add(entries, "Collision", "Smooth Manifold", SmoothManifoldSample::new, 2.0f, 20.0f, 21.0f);
        add(entries, "Collision", "Shape Cast", ShapeCastSample::new, 0.0f, 0.25f, 3.0f);
        add(entries, "Collision", "Time of Impact", TimeOfImpactSample::new, -16.0f, 45.0f, 5.0f);
        add(entries, "Continuous", "Bounce House", BounceHouseSample::new, 0.0f, 0.0f, 11.25f);
        add(entries, "Continuous", "Bounce Humans", BounceHumansSample::new, 0.0f, 0.0f, 12.0f);
        add(entries, "Continuous", "Chain Drop", ChainDropSample::new, 0.0f, 0.0f, 8.75f);
        add(entries, "Continuous", "Chain Slide", ChainSlideSample::new, 0.0f, 10.0f, 15.0f);
        add(entries, "Continuous", "Segment Slide", SegmentSlideSample::new, 0.0f, 10.0f, 15.0f);
        add(entries, "Continuous", "Skinny Box", SkinnyBoxSample::new, 1.0f, 5.0f, 6.25f);
        add(entries, "Continuous", "Ghost Bumps", GhostBumpsSample::new, 1.5f, 16.0f, 20.0f);
        add(entries, "Continuous", "Speculative Fallback", SpeculativeFallbackSample::new, 1.0f, 5.0f, 6.25f);
        add(entries, "Continuous", "Speculative Sliver", SpeculativeSliverSample::new, 0.0f, 1.75f, 2.5f);
        add(entries, "Continuous", "Speculative Ghost", SpeculativeGhostSample::new, 0.0f, 1.75f, 2.0f);
        add(entries, "Continuous", "Pixel Imperfect", PixelImperfectSample::new, 7.0f, 5.0f, 6.0f);
        add(entries, "Continuous", "Restitution Threshold", RestitutionThresholdSample::new, 7.0f, 5.0f, 6.0f);
        add(entries, "Continuous", "Drop", DropSample::new, 0.0f, 1.5f, 3.0f);
        add(entries, "Continuous", "Pinball", PinballSample::new, 0.0f, 9.0f, 12.5f);
        add(entries, "Continuous", "Wedge", WedgeSample::new, 0.0f, 5.5f, 6.0f);
        add(entries, "Determinism", "Falling Hinges", FallingHingesSample::new, 0.0f, 7.5f, 10.0f);
        add(entries, "Events", "Sensor Funnel", SensorFunnelSample::new, 0.0f, 0.0f, 33.325f);
        add(entries, "Events", "Sensor Bookend", SensorBookendSample::new, 0.0f, 6.0f, 7.5f);
        add(entries, "Events", "Foot Sensor", FootSensorSample::new, 0.0f, 6.0f, 7.5f);
        add(entries, "Events", "Contact", ContactEventSample::new, 0.0f, 0.0f, 43.75f);
        add(entries, "Events", "Platformer", PlatformerSample::new, 0.5f, 7.5f, 10.0f);
        add(entries, "Events", "Body Move", BodyMoveSample::new, 2.0f, 8.0f, 13.75f);
        add(entries, "Events", "Sensor Types", SensorTypesSample::new, 0.0f, 3.0f, 4.5f);
        add(entries, "Geometry", "Convex Hull", ConvexHullSample::new, 0.5f, 0.0f, 7.5f);
        add(entries, "Joints", "Distance Joint", DistanceJointSample::new, 0.0f, 12.0f, 8.75f);
        add(entries, "Joints", "Motor Joint", MotorJointSample::new, 0.0f, 7.0f, 10.0f);
        add(entries, "Joints", "Filter Joint", FilterJointSample::new, 0.0f, 7.0f, 10.0f);
        add(entries, "Joints", "Revolute", RevoluteJointSample::new, 0.0f, 15.5f, 17.5f);
        add(entries, "Joints", "Prismatic", PrismaticJointSample::new, 0.0f, 8.0f, 12.5f);
        add(entries, "Joints", "Wheel", WheelJointSample::new, 0.0f, 10.0f, 3.75f);
        add(entries, "Joints", "Bridge", BridgeSample::new, 0.0f, 0.0f, 62.5f);
        add(entries, "Joints", "Ball & Chain", BallAndChainSample::new, 0.0f, -8.0f, 27.5f);
        add(entries, "Joints", "Cantilever", CantileverSample::new, 0.0f, 0.0f, 8.75f);
        add(entries, "Joints", "Fixed Rotation", FixedRotationSample::new, 0.0f, 8.0f, 17.5f);
        add(entries, "Joints", "Breakable", BreakableJointSample::new, 0.0f, 8.0f, 17.5f);
        add(entries, "Joints", "Separation", JointSeparationSample::new, 0.0f, 8.0f, 25.0f);
        add(entries, "Joints", "User Constraint", UserConstraintSample::new, 3.0f, -1.0f, 3.75f);
        add(entries, "Joints", "Driving", DrivingSample::new, 0.0f, 5.0f, 10.0f);
        add(entries, "Joints", "Ragdoll", RagdollSample::new, 0.0f, 12.0f, 16.0f);
        add(entries, "Joints", "Soft Body", SoftBodySample::new, 0.0f, 5.0f, 6.25f);
        add(entries, "Joints", "Doohickey", DoohickeySample::new, 0.0f, 5.0f, 8.75f);
        add(entries, "Joints", "Scissor Lift", ScissorLiftSample::new, 0.0f, 9.0f, 10.0f);
        add(entries, "Joints", "Gear Lift", GearLiftSample::new, 0.0f, 6.0f, 7.0f);
        add(entries, "Joints", "Door", DoorSample::new, 0.0f, 0.0f, 4.0f);
        add(entries, "Joints", "Scale Ragdoll", ScaleRagdollSample::new, 0.0f, 4.5f, 6.0f);
        add(entries, "Robustness", "HighMassRatio1", HighMassRatio1Sample::new, 3.0f, 14.0f, 25.0f);
        add(entries, "Robustness", "HighMassRatio2", HighMassRatio2Sample::new, 0.0f, 16.5f, 25.0f);
        add(entries, "Robustness", "HighMassRatio3", HighMassRatio3Sample::new, 0.0f, 16.5f, 25.0f);
        add(entries, "Robustness", "Overlap Recovery", OverlapRecoverySample::new, 0.0f, 2.5f, 3.75f);
        add(entries, "Robustness", "Tiny Pyramid", TinyPyramidSample::new, 0.0f, 0.8f, 1.0f);
        add(entries, "Robustness", "Cart", CartSample::new, 0.0f, 1.0f, 1.5f);
        add(entries, "Shapes", "Chain Shape", ChainShapeSample::new, 0.0f, 0.0f, 43.75f);
        add(entries, "Shapes", "Compound Shapes", CompoundShapesSample::new, 0.0f, 6.0f, 12.5f);
        add(entries, "Shapes", "Filter", ShapeFilterSample::new, 0.0f, 5.0f, 12.5f);
        add(entries, "Shapes", "Custom Filter", CustomFilterSample::new, 0.0f, 5.0f, 10.0f);
        add(entries, "Shapes", "Restitution", RestitutionSample::new, 4.0f, 17.0f, 27.5f);
        add(entries, "Shapes", "Friction", FrictionSample::new, 0.0f, 14.0f, 15.0f);
        add(entries, "Shapes", "Rolling Resistance", RollingResistanceSample::new, 5.0f, 20.0f, 27.5f);
        add(entries, "Shapes", "Conveyor Belt", ConveyorBeltSample::new, 2.0f, 7.5f, 12.0f);
        add(entries, "Shapes", "Tangent Speed", TangentSpeedSample::new, 60.0f, -15.0f, 38.0f);
        add(entries, "Shapes", "Modify Geometry", ModifyGeometrySample::new, 0.0f, 5.0f, 6.25f);
        add(entries, "Shapes", "Chain Link", ChainLinkSample::new, 0.0f, 5.0f, 12.5f);
        add(entries, "Shapes", "Rounded", RoundedShapesSample::new, 2.0f, 8.0f, 13.75f);
        add(entries, "Shapes", "Ellipse", EllipseSample::new, 2.0f, 8.0f, 13.75f);
        add(entries, "Shapes", "Offset", OffsetShapesSample::new, 2.0f, 8.0f, 13.75f);
        add(entries, "Shapes", "Explosion", ExplosionSample::new, 0.0f, 0.0f, 14.0f);
        add(entries, "Shapes", "Recreate Static", RecreateStaticSample::new, 0.0f, 2.5f, 3.5f);
        add(entries, "World", "Large World", LargeWorldSample::new, LargeWorldSample.X_START, 15.0f, 25.0f);
        Collections.sort(entries, (a, b) -> {
            int category = a.category().compareTo(b.category());
            return category != 0 ? category : a.name().compareTo(b.name());
        });
        return Collections.unmodifiableList(entries);
    }

    private static void add(List<Box2DSampleEntry> entries, String category, String name,
            com.github.xpenatan.box2d.sample.shared.Box2DSampleFactory factory,
            float centerX, float centerY, float zoom) {
        entries.add(new Box2DSampleEntry(category, name, factory, new Box2DSampleCamera(centerX, centerY, zoom)));
    }
}
