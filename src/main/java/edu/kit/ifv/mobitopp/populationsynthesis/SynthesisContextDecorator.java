package edu.kit.ifv.mobitopp.populationsynthesis;

import java.io.File;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.DataRepositoryForPopulationSynthesis;
import edu.kit.ifv.mobitopp.data.DemandZoneRepository;
import edu.kit.ifv.mobitopp.data.local.configuration.DynamicParameters;
import edu.kit.ifv.mobitopp.data.local.configuration.PopulationSynthesisParser;
import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.MobilityProviderCustomerModel;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeType;
import edu.kit.ifv.mobitopp.result.ResultWriter;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;

public class SynthesisContextDecorator implements SynthesisContext {

  protected final SynthesisContext other;

  public SynthesisContextDecorator(SynthesisContext other) {
    super();
    this.other = other;
  }

  @Override
  public WrittenConfiguration configuration() {
    return other.configuration();
  }

  @Override
  public DynamicParameters experimentalParameters() {
    return other.experimentalParameters();
  }

  @Override
  public int maxIterations() {
    return other.maxIterations();
  }

  @Override
  public double maxGoodnessDelta() {
    return other.maxGoodnessDelta();
  }

  @Override
  public long seed() {
    return other.seed();
  }

  @Override
  public File carEngineFile() {
    return other.carEngineFile();
  }

  @Override
  public VisumNetwork network() {
    return other.network();
  }

  @Override
  public SimpleRoadNetwork roadNetwork() {
    return other.roadNetwork();
  }

  @Override
  public DemandZoneRepository zoneRepository() {
    return other.zoneRepository();
  }

  @Override
  public DataRepositoryForPopulationSynthesis dataRepository() {
    return other.dataRepository();
  }

  @Override
  public ImpedanceIfc impedance() {
    return other.impedance();
  }

  @Override
  public Map<String, MobilityProviderCustomerModel> carSharing() {
    return other.carSharing();
  }

  @Override
  public ResultWriter resultWriter() {
    return other.resultWriter();
  }

  @Override
  public void printStartupInformationOn(PrintStream out) {
    other.printStartupInformationOn(out);
  }

  @Override
  public List<AttributeType> attributes(RegionalLevel level) {
    return other.attributes(level);
  }

  @Override
  public PopulationSynthesisParser format() {
    return other.format();
  }

}