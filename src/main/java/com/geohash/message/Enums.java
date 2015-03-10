package com.geohash.message;

import java.io.Serializable;

import com.google.common.base.Objects;

import static com.geohash.message.Enums.TraceProtocol.*;

/**
 * User: mdyminski
 */
public class Enums implements Serializable {

    private static final long serialVersionUID = 6914843984024634583L;

    private TraceType traceType;

    private Direction direction;

    private Interface interfaceName;

    private TraceProtocol protocol;

    private ProtocolFormat protocolFormat;

    private ProtocolMessageType protocolMessageType;

    public TraceType getTraceType() {
        return traceType;
    }

    public void setTraceType(TraceType traceType) {
        this.traceType = traceType;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Interface getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(Interface interfaceName) {
        this.interfaceName = interfaceName;
    }

    public TraceProtocol getProtocol() {
        return protocol;
    }

    public void setProtocol(TraceProtocol protocol) {
        this.protocol = protocol;
    }

    public ProtocolFormat getProtocolFormat() {
        return protocolFormat;
    }

    public void setProtocolFormat(ProtocolFormat protocolFormat) {
        this.protocolFormat = protocolFormat;
    }

    public ProtocolMessageType getProtocolMessageType() {
        return protocolMessageType;
    }

    public void setProtocolMessageType(ProtocolMessageType protocolMessageType) {
        this.protocolMessageType = protocolMessageType;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("type", traceType).add("direction", direction)
                .add("interfaceName", interfaceName).add("protocol", protocol).add("protocolFormat", protocolFormat)
                .add("protocolMessageType", protocolMessageType).toString();
    }

    public enum TraceType {
        ENODEB(0), CELL_TRACE(1), UE_TRACE(2), IP_TRACE(3);

        private final int value;

        TraceType(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }

        public static TraceType fromValue(int value) {
            for (TraceType t : TraceType.values()) {
                if (t.value == value) {
                    return t;
                }
            }

            throw new IllegalArgumentException("Unknown TraceType:" + value);

        }

    }

    public enum Direction {

        UNDEFINED(0), DOWN(0x40), UP(0x80);

        private final int value;

        Direction(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }

        public static Direction fromShort(short value) {
            if ((value & DOWN.value) != 0) {
                return DOWN;
            } else if ((value & UP.value) != 0) {
                return UP;
            } else {
                return UNDEFINED;
            }
        }

        public static Direction fromValue(int value) {

            for (Direction d : Direction.values()) {
                if (d.value == value) {
                    return d;
                }
            }

            throw new IllegalArgumentException("Unknown direction:" + value);
        }
    }

    public enum Interface {

        UNDEFINED(0), S1(1), X2(2), E_UU(4);

        private final int value;

        Interface(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }

        public static Interface fromShort(short value) {
            if ((value & S1.value) != 0) {
                return S1;
            } else if ((value & X2.value) != 0) {
                return X2;
            } else if ((value & E_UU.value) != 0) {
                return E_UU;
            } else {
                return UNDEFINED;
            }
        }

        public static Interface fromValue(int value) {

            for (Interface d : Interface.values()) {
                if (d.value == value) {
                    return d;
                }
            }

            throw new IllegalArgumentException("Unknown interface:" + value);
        }
    }

    public enum ProtocolFormat {

        BCCH_BCH(0x0, "BCCH-BCH-Message"), BCCH_DL_SCH(0x1, "BCCH-DL-SCH-Message"), PCCH(0x2, "PCCH-Message"), DL_CCCH(
                0x3, "DL-CCCH-Message"), DL_DCCH(0x4, "DL-DCCH-Message"), UL_CCCH(0x5, "UL-CCCH-Message"), UL_DCCH(0x6,
                "UL-DCCH-Message"), TMP_PDU(0x10, "TMD PDU"), AMD_PDU(0x11, "AMD PDU"), AMD_PDU_SEGMENT(0x12,
                "AMD PDU segment"), STATUS_PDU(0x13, "STATUS PDU"), UMD_PDU_SN_5(0x14, "UMD PDU SN-5Bit"), UMD_PDU_SN_10(
                0x15, "UMD PDU SN-10Bit"), MAC_DL(0x20, "MAC DL/UL-SCH"), MAC_RAR(0x21, "MAC RandomAccessResp"), MAC_PDU(
                0x22, "MAC Transparent PDU"), MAC_TA(0x23, "MAC Instantaneous TA"), NOT_NEEDED(0xFF, "");

        private final int value;

        private final String name;

        ProtocolFormat(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public int value() {
            return value;
        }

        public String getName() {
            return name;
        }

        public static ProtocolFormat fromValue(int value) {
            for (ProtocolFormat t : ProtocolFormat.values()) {
                if (t.value == value) {
                    return t;
                }
            }

            throw new IllegalArgumentException("Unknown ProtocolFormat:" + value);
        }
    }

    public static enum TraceProtocol {

        UNDEFINED(0), S1AP(1), X2AP(2), RRC(4), RLC(8), MAC(16);

        private final int value;

        TraceProtocol(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }

        public static TraceProtocol fromValue(int value) {
            for (TraceProtocol t : TraceProtocol.values()) {
                if (t.value == value) {
                    return t;
                }
            }

            throw new IllegalArgumentException("Unknown TraceProtocol:" + value);
        }

        public static TraceProtocol fromShort(short value) {
            if ((value & S1AP.value) != 0) {
                return S1AP;
            } else if ((value & X2AP.value) != 0) {
                return X2AP;
            } else if ((value & RRC.value) != 0) {
                return RRC;
            } else if ((value & RLC.value) != 0) {
                return RLC;
            } else if ((value & MAC.value) != 0) {
                return MAC;
            } else {
                return UNDEFINED;
            }
        }
    }

    public static enum ProtocolMessageType {

        // @formatter:off

        // --------------------- S1AP ---------------------
        // S1AP messages traced in all trace types (Subscriber, Cell and Interface trace)
        PATH_SWITCH_REQUEST(0x0003,"PathSwitchRequest", S1AP),
        PATH_SWITCHREQUEST_ACKNOWLEDGE(0x0103,"PathSwitchRequestAcknowledge", S1AP),
        PATH_SWITCHREQUEST_FAILURE(0x0203,"PathSwitchRequestFailure", S1AP),
        E_RABSETUP_REQUEST(0x0005,"E-RABSetupRequest", S1AP),
        E_RABSETUP_RESPONSE(0x0105,"E-RABSetupResponse", S1AP),
        E_RABMODIFY_REQUEST(0x0006,"E-RABModifyRequest", S1AP),
        E_RABMODIFY_RESPONSE(0x0106,"E-RABModifyResponse", S1AP),
        E_RABRELEASE_COMMAND(0x0007,"E-RABReleaseCommand", S1AP),
        E_RABRELEASE_RESPONSE(0x0107,"E-RABReleaseResponse", S1AP),
        E_RABRELEASE_INDICATION(0x0008,"E-RABReleaseIndication", S1AP),
        INITIAL_CONTEXTSETUP_RESPONSE(0x0109,"InitialContextSetupResponse", S1AP),
        INITIAL_CONTEXTSETUP_FAILURE(0x0209,"InitialContextSetupFailure", S1AP),
        DOWNLINK_NASTRANSPORT(0x000B,"DownlinkNASTransport", S1AP),
        HANDOVER_REQUIRED(0x0000, "HandoverRequired",  S1AP),
        HANDOVER_COMMAND(0x0100, "HandoverCommand",  S1AP),
        S1AP_HANDOVER_PREPARATION_FAILURE(0x0200, "HandoverPreparationFailure",  S1AP),
        S1AP_HANDOVER_REQUEST_ACKNOWLEDGE(0x0101, "HandoverRequestAcknowledge",  S1AP),
        HANDOVER_FAILURE(0x0201, "HandoverFailure",  S1AP),
        HANDOVER_NOTIFY(0x0002,"HandoverNotify", S1AP),
        S1AP_HANDOVER_CANCEL(0x0004,"HandoverCancel", S1AP),
        HANDOVER_CANCEL_ACKNOWLEDGE(0x0104,"HandoverCancelAcknowledge", S1AP),
        UPLINK_NASTRANSPORT(0x000D,"UplinkNASTransport", S1AP),
        S1AP_ERROR_INDICATION(0x000F,"ErrorIndication", S1AP),
        NASNON_DELIVERY_INDICATION(0x0010,"NASNonDeliveryIndication", S1AP),
        UECONTEXT_RELEASE_REQUEST(0x0012,"UEContextReleaseRequest", S1AP),
        DOWNLINK_S1CDMA2000TUNNELING(0x0013,"DownlinkS1cdma2000tunneling", S1AP),
        UPLINK_S1CDMA2000TUNNELING(0x0014,"UplinkS1cdma2000tunneling", S1AP),
        UECONTEXT_MODIFICATION_REQUEST(0x0015,"UEContextModificationRequest", S1AP),
        UECONTEXT_MODIFICATION_RESPONSE(0x0115,"UEContextModificationResponse", S1AP),
        UECONTEXT_MODIFICATION_FAILURE(0x0215,"UEContextModificationFailure", S1AP),
        UECAPABILITY_INFO_INDICATION(0x0016,"UECapabilityInfoIndication", S1AP),
        UECONTEXT_RELEASE_COMMAND(0x0017,"UEContextReleaseCommand", S1AP),
        UECONTEXT_RELEASE_COMPLETE(0x0117,"UEContextReleaseComplete", S1AP),
        ENBSTATUS_TRANSFER(0x0018,"ENBStatusTransfer", S1AP),
        MMESTATUS_TRANSFER(0x0019,"MMEStatusTransfer", S1AP),
        DEACTIVATE_TRACE(0x001A,"DeactivateTrace", S1AP),
        TRACE_FAILURE_INDICATION(0x001C,"TraceFailureIndication", S1AP),
        LOCATION_REPORTING_CONTROL(0x001F,"LocationReportingControl", S1AP),
        LOCATION_REPORTINGFAILURE_INDICATION(0x0020,"LocationReportingFailureIndication", S1AP),
        LOCATION_REPOR(0x0021,"LocationReport", S1AP),
        CELL_TRAFFIC_TRACE(0x002A,"CellTrafficTrace", S1AP),

        // S1AP messages traced only in case of Cell and Interface trace
        S1AP_HANDOVER_REQUEST(0x2001,"HandoverRequest", S1AP),
        INITIAL_CONTEXTSETUP_REQUEST(0x0009,"InitialContextSetupRequest", S1AP),
        INITIAL_UEMESSAGE(0x000C,"InitialUEMessage", S1AP),
        TRACE_START(0x001B,"TraceStart", S1AP),

        // S1AP messages traced only in case of Interface trace
        S1AP_PAGING(0x200A,"Paging", S1AP),
        RESET(0x200E,"Reset", S1AP),
        RESET_ACKNOWLEDGE(0x210E,"ResetAcknowledge", S1AP),
        S1SETUPREQUEST(0x2011,"S1SetupRequest", S1AP),
        S1SETUPRESPONSE(0x2111,"S1SetupResponse", S1AP),
        S1SETUPFAILURE(0x2211,"S1SetupFailure", S1AP),
        S1AP_ENBCONFIGURATION_UPDATE(0x201D,"ENBConfigurationUpdate", S1AP),
        S1AP_ENBCONFIGURATION_UPDATE_ACKNOWLEDGE(0x211D,"ENBConfigurationUpdateAcknowledge", S1AP),
        S1AP_ENBCONFIGURATION_UPDATE_FAILURE(0x221D,"ENBConfigurationUpdateFailure", S1AP),
        MMECONFIGURATION_UPDATE(0x201E,"MMEConfigurationUpdate", S1AP),
        MMECONFIGURATION_UPDATE_ACKNOWLEDGE(0x211E,"MMEConfigurationUpdateAcknowledge", S1AP),
        MMECONFIGURATION_UPDATE_FAILURE(0x221E,"MMEConfigurationUpdateFailure", S1AP),
        OVERLOAD_START(0x2022,"OverloadStart", S1AP),
        OVERLOAD_STOP(0x2023,"OverloadStop", S1AP),
        WRITE_REPLACEWARNING_REQUEST(0x2024,"WriteReplaceWarningRequest", S1AP),
        WRITE_REPLACEWARNING_RESPONSE(0x2124,"WriteReplaceWarningResponse", S1AP),
        ENBCONFIGURATION_TRANSFER(0x2028,"ENBConfigurationTransfer", S1AP),
        MMECONFIGURATION_TRANSFER(0x2029,"MMEConfigurationTransfer", S1AP),
        KILL_REQUEST(0x202B,"KillRequest", S1AP),
        KILL_RESPONSE(0x212B,"KillResponse", S1AP),

        // --------------------- X2AP ---------------------
        // X2AP messages traced in all trace types (Subscriber, Cell and Interface trace)
        X2AP_HANDOVER_REQUEST_ACKNOWLEDGE(0x0100,"HandoverRequestAcknowledge", X2AP),
        X2AP_HANDOVER_PREPARATION_FAILURE(0x0200,"HandoverPreparationFailure", X2AP),
        X2AP_HANDOVER_CANCEL(0x0001,"HandoverCancel", X2AP),
        X2AP_ERROR_INDICATION(0x0003,"ErrorIndication", X2AP),
        SNSTATUS_TRANSFER(0x0004,"SNStatusTransfer", X2AP),
        UECONTEXT_RELEASE(0x0005,"UEContextRelease", X2AP),

        // X2 messages traced only in case of Cell and Interface trace
        X2AP_HANDOVER_REQUEST(0x2000,"HandoverRequest", X2AP),
        HANDOVER_CANCELNON_UE(0x2001,"HandoverCancelNonUE", X2AP),
        RLFINDICATION(0x200D,"RLFIndication", X2AP),
        HANDOVER_REPORT(0x200E,"HandoverReport", X2AP),

        // X2 messages traced only in case of Interface trace
        X2SETUPREQUEST(0x2006,"X2SetupRequest", X2AP),
        X2SETUPRESPONSE(0x2106,"X2SetupResponse", X2AP),
        X2SETUPFAILURE(0x2206,"X2SetupFailure", X2AP),
        RESET_REQUEST(0x2007,"ResetRequest", X2AP),
        RESET_RESPONSE(0x2107,"ResetResponse", X2AP),
        X2AP_ENBCONFIGURATION_UPDATE(0x2008,"ENBConfigurationUpdate", X2AP),
        X2AP_ENBCONFIGURATION_UPDATE_ACKNOWLEDGE(0x2108,"ENBConfigurationUpdateAcknowledge", X2AP),
        X2AP_ENBCONFIGURATION_UPDATE_FAILURE(0x2208,"ENBConfigurationUpdateFailure", X2AP),
        RESOURCE_STATUS_REQUEST(0x2009,"ResourceStatusRequest", X2AP),
        RESOURCE_STATUS_RESPONSE(0x2109,"ResourceStatusResponse", X2AP),
        RESOURCE_STATUS_FAILURE(0x2209,"ResourceStatusFailure", X2AP),
        RESOURCE_STATUS_UPDATE(0x200A,"ResourceStatusUpdate", X2AP),
        MOBILITY_CHANGE_REQUEST(0x200C,"MobilityChangeRequest", X2AP),
        MOBILITY_CHANGE_ACKNOWLEDGE(0x210C,"MobilityChangeAcknowledge", X2AP),
        MOBILITY_CHANGE_FAILURE(0x220C,"MobilityChangeFailure", X2AP),

        // --------------------- RRC ---------------------
        // RRC messages traced in all trace types (Subscriber, Cell and Interface trace)
        SECURITY_MODE_COMMAND(0x0006, "SecurityModeCommand (DL-DCCH)",  RRC),
        SECURITY_MODE_COMPLETE(0x0106, "SecurityModeComplete (UL-DCCH)", RRC),
        SECURITY_MODE_FAILURE(0x0206, "SecurityModeFailure (UL-DCCH)", RRC),
        RRC_CONNECTION_RECONFIGURATION(0x0007, "RRCConnectionReconfiguration (DL-DCCH)", RRC),
        RRC_CONNECTION_RECONFIGURATION_COMPLETE(0x0107, "RRCConnectionReconfigurationComplete (UL-DCCH)", RRC),
        RRC_CONNECTION_REESTABLISHMENT_REQUEST(0x0009, "RRCConnectionReestablishmentRequest (UL-CCCH)", RRC),
        RRC_CONNECTION_REESTABLISHMENT_REJECT(0x0209, "RRCConnectionReestablishmentReject (DL-CCCH)", RRC),
        RRC_CONNECTION_REESTABLISHMENT(0x000A, "RRCConnectionReestablishment (DL-CCCH)", RRC),
        RRC_CONNECTION_REESTABLISHMENT_COMPLETE(0x010A, "RRCConnectionReestablishmentComplete (UL-DCCH)", RRC),
        RRC_CONNECTION_RELEASE(0x000B, "RRCConnectionRelease (DL-DCCH)", RRC),
        MOBILITY_FROM_EUTRA_COMMAND(0x000D, "MobilityFromEUTRACommand (DL-DCCH)", RRC),
        HANDOVER_FROM_EUTRA_PREPARATION_REQUEST(0x000E, "HandoverFromEUTRAPreparation Request (DL-DCCH)", RRC),
        UL_HANDOVER_PREPARATION_TRANSFER(0x000F, "ULHandoverPreparationTransfer (UL-DCCH)", RRC),
        MEASUREMENT_REPORT(0x0011, "MeasurementReport (UL-DCCH)", RRC),
        DL_INFORMATION_TRANSFER(0x0012, "DLInformationTransfer (DL-DCCH)", RRC),
        UL_INFORMATION_TRANSFER(0x0013, "ULInformationTransfer (UL-DCCH)", RRC),
        UE_CAPABILITY_ENQUIRY(0x0014, "UECapabilityEnquiry (DL-DCCH)", RRC),
        UE_CAPABILITY_INFORMATION(0x0114, "UECapabilityInformation (UL-DCCH)", RRC),
        UE_INFORMATION_REQUEST(0x0016, "UEInformationRequest (DL-DCCH)", RRC),
        UE_INFORMATION_RESPONSE(0x0116, "UEInformationResponse (UL-DCCH)", RRC),
        CSFB_PARAMETERS_REQUEST_CDMA2000(0x0015, "CSFBParametersRequestCDMA2000 (UL-DCCH)", RRC),
        CSFB_PARAMETERS_RESPONSE_CDMA2000(0x0115, "CSFBParametersResponseCDMA2000 (DL-DCCH)", RRC),

        // RRC messages traced in case of only Cell and Interface trace
        RRC_CONNECTION_REQUEST(0x0004, "RRCConnectionRequest (UL-CCCH)", RRC),
        RRC_CONNECTION_REJECT(0x0204, "RRCConnectionReject (DL-CCCH)", RRC),
        RRC_CONNECTION_SETUP(0x0005, "RRCConnectionSetup (DL-CCCH)", RRC),
        RRC_CONNECTION_SETUP_COMPLETE(0x0105, "RRCConnectionSetupComplete (UL-DCCH)", RRC),
        MASTER_INFORMATION_BLOCK(0x2000, "MasterInformationBlock (BCCH-BCH)", RRC),
        SYSTEM_INFORMATION_BLOCK_TYPE1(0x2001, "SystemInformationBlockType1 (BCCH-DL-SCH)", RRC),
        SYSTEM_INFORMATION(0x2002, "SystemInformation (BCCH-DL-SCH)", RRC),
        RRC_PAGING(0x2003, "Paging (PCCH)", RRC),

        // MAC traced in case of only Cell and Interface trace
        MAC_OTHER(0x0000, "MACother", MAC),
        INSTANTANEOUS_TA(0x0001, "InstantaneousTA", MAC);

        // @formatter:on

        private final int value;

        private final String name;

        private final TraceProtocol protocol;

        ProtocolMessageType(int value, String name, TraceProtocol protocol) {
            this.value = value;
            this.name = name;
            this.protocol = protocol;
        }

        public static ProtocolMessageType fromValue(int value, TraceProtocol protocol) {
            for (ProtocolMessageType t : ProtocolMessageType.values()) {
                if (t.value == value && t.protocol == protocol) {
                    return t;
                }
            }

            throw new IllegalArgumentException("Unknown ProtocolMessageType: " + value + " and Protocol: " + protocol);
        }

        public int value() {
            return value;
        }

        public String stringName() {
            return name;
        }

        public TraceProtocol getProtocol() {
            return protocol;
        }
    }
}

