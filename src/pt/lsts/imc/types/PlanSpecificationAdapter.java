/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2016, Laboratório de Sistemas e Tecnologia Subaquática
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     - Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     - Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     - Neither the names of IMC, LSTS, IMCJava nor the names of its 
 *       contributors may be used to endorse or promote products derived from 
 *       this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL LABORATORIO DE SISTEMAS E TECNOLOGIA SUBAQUATICA
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 * $Id:: CsvExporter.java 149 2012-05-29 18:24:53Z zepinto                     $:
 */
package pt.lsts.imc.types;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import pt.lsts.imc.Goto;
import pt.lsts.imc.Goto.Z_UNITS;
import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.PlanSpecification;

/**
 * This class holds a PlanSpecification data structure that is independent of IMC version
 * @author zp
 */
public class PlanSpecificationAdapter implements IMessageAdapter {

    protected IMCMessage data = null;
    protected LinkedHashMap<String, IMCMessage> maneuvers = new LinkedHashMap<String, IMCMessage>();
    protected Vector<Transition> transitions = new Vector<Transition>();
    protected Vector<IMCMessage> variables = new Vector<IMCMessage>();
    protected Vector<IMCMessage> planStartActions = new Vector<IMCMessage>();
    protected Vector<IMCMessage> planEndActions = new Vector<IMCMessage>();
    protected LinkedHashMap<String, Vector<IMCMessage>> maneuverStartActions = new LinkedHashMap<String, Vector<IMCMessage>>();
    protected LinkedHashMap<String, Vector<IMCMessage>> maneuverEndActions = new LinkedHashMap<String, Vector<IMCMessage>>();
    protected String planId = "", description = "", firstManeuverId = "", vnamespace = "";

    @Override
    public Collection<String> getCompatibleMessages() {
        return Arrays.asList("PlanSpecification", "MissionSpecification");
    }

    /**
     * Create a new Plan by parsing the IMC message (PlanSpecification)
     * @param data A PlanSpecification or MissionSpecification message
     */
    public PlanSpecificationAdapter(IMCMessage data) {
        setData(data);
    }

    /**
     * Create a new empty plan
     */
    public PlanSpecificationAdapter() {
        this(new PlanSpecification());
    }

    protected void parseData() {
        this.planId = data.getString("plan_id");
        this.description = data.getString("description");
        this.firstManeuverId = data.getString("start_man_id");
        this.vnamespace = data.getString("vnamespace");

        this.planStartActions = data.getMessageList("start_actions");
        this.planEndActions = data.getMessageList("end_actions");

        Object o = data.getValue("maneuvers");

        // pre-IMC5
        if (o instanceof IMCMessage) {
            IMCMessage m = (IMCMessage) o;
            while (m != null && m.getAbbrev().equals("ManeuverSpecification")) {
                String manId = m.getString("maneuver_id");

                IMCMessage transition = m.getMessage("transitions");
                while (transition != null) {
                    transitions.add(new Transition(manId, transition.getString("dest_man"), transition
                            .getString("conditions"), transition.getString("actions")));
                    transition = transition.getMessage("next");
                }
                maneuvers.put(manId, m.getMessage("data"));
                Vector<IMCMessage> startActions = m.getMessageList("start_actions");
                Vector<IMCMessage> endActions = m.getMessageList("end_actions");
                if (startActions != null && !startActions.isEmpty())
                    maneuverStartActions.put(manId, startActions);
                if (endActions != null && !endActions.isEmpty())
                    maneuverEndActions.put(manId, endActions);

                m = m.getMessage("next");
            }
        }
        // post-IMC5
        else if (o instanceof Collection<?>) {
            Collection<?> mans = (Collection<?>) o;

            for (Object ob : mans) {
                if (ob == null || !(ob instanceof IMCMessage))
                    continue;
                IMCMessage m = (IMCMessage) ob;
                String manId = m.getString("maneuver_id");
                Vector<IMCMessage> startActions = m.getMessageList("start_actions");
                Vector<IMCMessage> endActions = m.getMessageList("end_actions");
                if (startActions != null && !startActions.isEmpty())
                    maneuverStartActions.put(manId, startActions);
                if (endActions != null && !endActions.isEmpty())
                    maneuverEndActions.put(manId, endActions);
                maneuvers.put(manId, m.getMessage("data"));
            }

            Collection<?> trans = (Collection<?>) data.getValue("transitions");

            for (Object ob : trans) {
                if (ob == null || !(ob instanceof IMCMessage))
                    continue;
                IMCMessage t = (IMCMessage) ob;

                transitions.add(new Transition(t.getString("source_man"), t.getString("dest_man"), 
                        t.getString("conditions"), t.getMessageList("actions")));
            }
        }
    }

    protected IMCMessage generateTransitionsPreImc5(IMCDefinition defs, Vector<Transition> manTransitions) {
        Vector<IMCMessage> result = new Vector<IMCMessage>();
        for (Transition t : manTransitions) {
            IMCMessage maneuverTransition = defs.create("ManeuverTransition");
            maneuverTransition.setValue("source_man", t.source_man);
            maneuverTransition.setValue("dest_man", t.dest_man);
            maneuverTransition.setValue("conditions", t.conditions);
            maneuverTransition.setValue("actions", t.stringActions);
            result.add(maneuverTransition);
        }

        // link all messages
        for (int i = 0; i < result.size()-1; i++)
            result.get(i).setValue("next", result.get(i+1));        
        if (!result.isEmpty())
            return result.firstElement();
        return null;
    }

    protected IMCMessage generateManeuversPreImc5(IMCDefinition defs) {
        Vector<IMCMessage> result = new Vector<IMCMessage>();

        for (String manId : maneuvers.keySet()) {
            IMCMessage manSpec = defs.create("ManeuverSpecification", "maneuver_id", manId);
            try {
                IMCMessage m = defs.replicate(maneuvers.get(manId));
                manSpec.setValue("data", m);
                if (maneuverStartActions.containsKey(manId))
                    manSpec.setMessageList(maneuverStartActions.get(manId), "start_actions");
                if (maneuverEndActions.containsKey(manId))
                    manSpec.setMessageList(maneuverEndActions.get(manId), "end_actions");
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            Vector<Transition> outgoing = getOutgoingTransitions(manId);
            manSpec.setValue("num_transitions", outgoing.size());
            manSpec.setValue("transitions", generateTransitionsPreImc5(defs, outgoing));
            result.add(manSpec);
        }

        // link all messages
        for (int i = 0; i < result.size()-1; i++)
            result.get(i).setValue("next", result.get(i+1));        

        if (!result.isEmpty())
            return result.firstElement();
        return null;    
    }

    protected Vector<IMCMessage> generateTransitions(IMCDefinition defs) {
        Vector<IMCMessage> result = new Vector<IMCMessage>();

        for (Transition t : transitions) {
            IMCMessage planTransition = defs.create("PlanTransition");
            planTransition.setValue("source_man", t.source_man);
            planTransition.setValue("dest_man", t.dest_man);
            planTransition.setValue("conditions", t.conditions);
            planTransition.setValue("actions", t.msgActions);
            result.add(planTransition);
        }

        return result;
    }

    protected Vector<IMCMessage> generateManeuvers(IMCDefinition defs) {
        Vector<IMCMessage> result = new Vector<IMCMessage>();

        for (String manId : maneuvers.keySet()) {
            IMCMessage planManeuver = defs.create("PlanManeuver", "maneuver_id", manId);
            planManeuver.setValue("data", maneuvers.get(manId));
            if (maneuverStartActions.containsKey(manId))
                planManeuver.setMessageList(maneuverStartActions.get(manId), "start_actions");
            if (maneuverEndActions.containsKey(manId))
                planManeuver.setMessageList(maneuverEndActions.get(manId), "end_actions");
            result.add(planManeuver);
        }
        return result;
    }      
    
    public final Vector<IMCMessage> getManeuverStartActions(String manId) {
        return maneuverStartActions.get(manId);
    }
    
    public final Vector<IMCMessage> getManeuverEndActions(String manId) {
        return maneuverEndActions.get(manId);
    }
    
    public void setManeuverStartActions(String manId, Vector<IMCMessage> messages) {
        maneuverStartActions.put(manId, messages);
    }
    
    public void setManeuverEndActions(String manId, Vector<IMCMessage> messages) {
        maneuverEndActions.put(manId, messages);
    }
    
    protected void generateMessage(IMCDefinition defs) {
        boolean preIMC4 = defs.getMessageId("MissionSpecification") != -1;
        boolean preIMC5 = defs.getMessageId("PlanManeuver") == -1;

        IMCMessage result = null;

        if (preIMC4)
            result = defs.create("MissionSpecification");
        else
            result = defs.create("PlanSpecification");

        result.setValue("plan_id", getPlanId());
        result.setValue("mission_id", getPlanId());
        result.setValue("description", getDescription());
        result.setValue("start_man_id", getFirstManeuverId());
        result.setValue("vnamespace", getVnamespace());

        if (preIMC5) {
            result.setValue("num_maneuvers", maneuvers.size());
            result.setValue("maneuvers", generateManeuversPreImc5(defs));             
        }
        else {
            result.setValue("variables", variables);
            result.setValue("maneuvers", generateManeuvers(defs));
            result.setValue("transitions", generateTransitions(defs));
        }

        data = result;         
    }

    /**
     * @return the data
     */
    public IMCMessage getData(IMCDefinition defs) {
        generateMessage(defs);
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(IMCMessage data) {
        this.data = data;
        parseData();
    }

    /**
     * @return the planId
     */
    public String getPlanId() {
        return planId;
    }

    /**
     * @param planId the planId to set
     */
    public void setPlanId(String planId) {
        this.planId = planId;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the vnamespace
     */
    public String getVnamespace() {
        return vnamespace;
    }

    /**
     * @param vnamespace the vnamespace to set
     */
    public void setVnamespace(String vnamespace) {
        this.vnamespace = vnamespace;
    }

    /**
     * @return the firstManeuverId
     */
    public String getFirstManeuverId() {
        return firstManeuverId;
    }

    /**
     * @param firstManeuverId the firstManeuverId to set
     */
    public void setFirstManeuverId(String firstManeuverId) {
        this.firstManeuverId = firstManeuverId;
    }

    /**
     * Add a transition to this plan
     * @param source_man The id of the source maneuver
     * @param dest_man The id of the target maneuver
     * @param conditions The conditions for the transition to take place
     * @param actions The actions triggered on transition
     */
    public void addTransition(String source_man, String dest_man, String conditions, String actions) {
        transitions.add(new Transition(source_man, dest_man, conditions, actions));
    }

    /**
     * Retrieve all transitions going out of maneuver with given id 
     * @param source_man The id of the source maneuver
     * @return All the transitions whose source maneuver matches the given parameter
     */
    public final Vector<Transition> getOutgoingTransitions(String source_man) {
        Vector<Transition> ret = new Vector<PlanSpecificationAdapter.Transition>();
        for (Transition t : transitions) {
            if (t.source_man.equals(source_man))
                ret.add(t);
        }
        return ret;
    }



    /**
     * Add a maneuver to this plan
     * @param id The id of the maneuver to be added
     * @param maneuver The maneuver data (IMCMessage)
     */
    public void addManeuver(String id, IMCMessage maneuver) {
        if (firstManeuverId == null || firstManeuverId.isEmpty())
            firstManeuverId = id;
        maneuvers.put(id, maneuver);
    }

    /**
     * Retrieve a maneuver from this plan
     * @param maneuver_id The id of the maneuver to be retrieved
     * @return The maneuver with given id or <b>null</b> if no such maneuver exists
     */
    public IMCMessage getManeuver(String maneuver_id) {
        return maneuvers.get(maneuver_id);
    }

    public final Map<String, IMCMessage> getAllManeuvers() {
        return maneuvers;
    }

    public final Collection<Transition> getAllTransitions() {
        return transitions;
    }


    /**
     * @return the variables
     */
    public final Vector<IMCMessage> getVariables() {
        return variables;
    }

    /**
     * @param variables the variables to set
     */
    public final void setVariables(Vector<IMCMessage> variables) {
        this.variables = variables;
    }

    /**
     * @return the planStartActions
     */
    public final Vector<IMCMessage> getPlanStartActions() {
        return planStartActions;
    }

    /**
     * @param planStartActions the planStartActions to set
     */
    public final void setPlanStartActions(Vector<IMCMessage> planStartActions) {
        this.planStartActions = planStartActions;
    }

    /**
     * @return the planEndActions
     */
    public final Vector<IMCMessage> getPlanEndActions() {
        return planEndActions;
    }

    /**
     * @param planEndActions the planEndActions to set
     */
    public final void setPlanEndActions(Vector<IMCMessage> planEndActions) {
        this.planEndActions = planEndActions;
    }
    
    

    public static void main(String[] args) {

        PlanSpecificationAdapter plan = new PlanSpecificationAdapter();
        plan.setDescription("Plan generated");
        plan.setPlanId("the_plan_id");
        plan.addManeuver("Goto1", 
                new Goto()
        			.setSpeed(1000) 
        			.setLat(Math.toRadians(41)) 
        			.setLon(Math.toRadians(-8)) 
        			.setZ(2f)
        			.setZUnits(Z_UNITS.DEPTH)
        			.setSpeedUnits(pt.lsts.imc.Goto.SPEED_UNITS.METERS_PS)
        		);
        plan.addManeuver("Goto2", 
        		new Goto()
	        		.setSpeed(1000) 
	    			.setLat(Math.toRadians(41)) 
	    			.setLon(Math.toRadians(-8)) 
	    			.setZ(5f)
	    			.setZUnits(Z_UNITS.DEPTH)
	    			.setSpeedUnits(pt.lsts.imc.Goto.SPEED_UNITS.METERS_PS)
        		);
    			plan.addTransition("Goto1", "Goto2", "ManeuverIsDone", null);

    }

    public class Transition {
        protected String source_man, dest_man, conditions, stringActions;
        protected Vector<IMCMessage> msgActions;

        public Transition(String source_man, String dest_man, String conditions, String actions) {
            this.source_man = source_man;
            this.dest_man = dest_man;
            this.conditions = conditions;
            setActions(actions);
        }

        public Transition(String source_man, String dest_man, String conditions, Vector<IMCMessage> actions) {
            this.source_man = source_man;
            this.dest_man = dest_man;
            this.conditions = conditions;
            setActions(actions);
        }

        public void setActions(String actions) {
            this.stringActions = actions;
            this.msgActions = new Vector<IMCMessage>();
        }

        public void setActions(Vector<IMCMessage> actions) {
            this.stringActions = "";
            for (IMCMessage m : actions)
                stringActions += m.toString();            
            this.msgActions = actions;
        }

        /**
         * @return the source_man
         */
        public String getSourceManeuver() {
            return source_man;
        }

        /**
         * @param source_man the source_man to set
         */
        public void setSourceManeuver(String source_man) {
            this.source_man = source_man;
        }

        /**
         * @return the dest_man
         */
        public String getDestManeuver() {
            return dest_man;
        }

        /**
         * @param dest_man the dest_man to set
         */
        public void setDestManeuver(String dest_man) {
            this.dest_man = dest_man;
        }

        /**
         * @return the conditions
         */
        public String getConditions() {
            return conditions;
        }

        /**
         * @param conditions the conditions to set
         */
        public void setConditions(String conditions) {
            this.conditions = conditions;
        }
    }
}
